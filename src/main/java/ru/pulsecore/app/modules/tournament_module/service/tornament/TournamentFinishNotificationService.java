package ru.pulsecore.app.modules.tournament_module.service.tornament;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.tournament_module.client.PlayerClient;
import ru.pulsecore.app.modules.tournament_module.entity.PlayerNotification;
import ru.pulsecore.app.modules.notification_modules.application.WebPushService;
import ru.pulsecore.app.modules.shared.util.push.PushMessageBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishNotificationService {

    //todo отправлять ивент
    private final WebPushService webPushService;
    private final NotificationPermissionService notificationPermissionService;
    private final PlayerClient playerClient;

    public void sendCancelled(List<PlayerNotification> notifications) {
        for (PlayerNotification pn : notifications) {
            var player = playerClient.getPlayerById(pn.getPlayerId());
            var tournament = pn.getTournament();

            log.info("❌ Tournament cancelled: player={}, tournament={}",
                    player.playerId(), tournament.getId());

            if (notificationPermissionService.canSendPush(player.playerId())) {
                String time = tournament.getTime() != null ? tournament.getTime() : "?";
                String date = tournament.getDate() != null ? tournament.getDate().toString() : "?";
                webPushService.sendToPlayer(
                        player.playerId(),
                        "❌ Турнир отменён!",
                        PushMessageBuilder.buildCancelledBody(date, time),
                        "/dashboard"
                );
            }
        }
        log.debug("📩 Cancelled notifications: {}", notifications.size());
    }
}