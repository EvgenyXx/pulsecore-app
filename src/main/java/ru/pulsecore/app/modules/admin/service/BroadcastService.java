// modules/admin/service/BroadcastService.java
package ru.pulsecore.app.modules.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.push.service.WebPushService;
import ru.pulsecore.app.modules.shared.service.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.shared.service.mail.MailTypes;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BroadcastService {

    private static final String PUSH_TITLE = "PulseCore";
    private static final String PUSH_URL = "/dashboard";

    private final PlayerRepository playerRepository;
    private final WebPushService webPushService;
    private final MailStrategyRegistry mailStrategyRegistry;

    public BroadcastResult broadcast(String message) {
        List<Player> players = playerRepository.findByVerifiedTrueAndIsBlockedFalse();

        int pushSent = 0;
        int emailSent = 0;

        for (Player player : players) {
            if (sendPush(player, message)) pushSent++;
            if (sendEmail(player, message)) emailSent++;
        }

        log.info("Рассылка завершена. Push: {}, Email: {}", pushSent, emailSent);

        return new BroadcastResult(players.size(), pushSent, emailSent);
    }

    private boolean sendPush(Player player, String message) {
        if (!player.isPushEnabled()) return false;
        try {
            webPushService.sendToPlayer(player.getId(), PUSH_TITLE, message, PUSH_URL);
            return true;
        } catch (Exception e) {
            log.error("Push не отправлен playerId={}: {}", player.getId(), e.getMessage());
            return false;
        }
    }

    private boolean sendEmail(Player player, String message) {
        if (player.getEmail() == null || player.getEmail().isBlank()) return false;
        try {
            mailStrategyRegistry.send(MailTypes.BROADCAST, player.getEmail(), message);
            return true;
        } catch (Exception e) {
            log.error("Email не отправлен playerId={}: {}", player.getId(), e.getMessage());
            return false;
        }
    }
}