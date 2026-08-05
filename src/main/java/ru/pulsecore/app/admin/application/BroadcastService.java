package ru.pulsecore.app.admin.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.admin.infrastructure.clinet.PlayerClient;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.BroadcastContext;
import ru.pulsecore.app.shared.dto.MailNotificationEvent;
import ru.pulsecore.app.shared.dto.PlayerData;
import ru.pulsecore.app.shared.dto.PushNotificationEvent;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BroadcastService {

    private static final String PUSH_TITLE = "PulseCore";
    private static final String PUSH_URL = "/dashboard";

    private final PlayerClient playerClient;
    private final ApplicationEventPublisher eventPublisher;

    public BroadcastResult broadcast(String message) {
        List<PlayerData> players = playerClient.getPlayers();

        int pushSent = 0;
        int emailSent = 0;

        for (var player : players) {
            if (sendPush(player.playerId(), message)) pushSent++;
            if (sendEmail(player.email(), message)) emailSent++;
        }

        log.info("Рассылка завершена. Push: {}, Email: {}", pushSent, emailSent);

        return new BroadcastResult(players.size(), pushSent, emailSent);
    }

    private boolean sendPush(UUID playerId, String message) {
        try {
            eventPublisher.publishEvent(
                    new PushNotificationEvent(
                            playerId, PUSH_TITLE, message, PUSH_URL
                    )
            );
            return true;
        } catch (Exception e) {
            log.error("Ошибка при отправке пуш уведомления {}", e.getMessage());
            return false;
        }
    }

    private boolean sendEmail(String email, String message) {
        try {
            eventPublisher.publishEvent(
                    new MailNotificationEvent(
                            MailTypes.BROADCAST,
                            new BroadcastContext(email, message)
                    )
            );
            return true;
        } catch (Exception e) {
            log.error("Ошибка при отправке email {}", e.getMessage());
            return false;
        }
    }
}