package ru.pulsecore.app.tournament.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.entity.PlayerNotification;
import ru.pulsecore.app.notification.application.WebPushService;
import ru.pulsecore.app.shared.util.PushMessageBuilder;

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