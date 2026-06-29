// TournamentFinishNotificationService.java
package ru.pulsecore.app.modules.notification.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.notification.service.NotificationPermissionService;
import ru.pulsecore.app.modules.push.service.WebPushService;
import ru.pulsecore.app.modules.shared.util.push.PushMessageBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishNotificationService {

    private final WebPushService webPushService;
    private final NotificationPermissionService notificationPermissionService;

    public void sendCancelled(List<PlayerNotification> notifications) {
        for (PlayerNotification pn : notifications) {
            var player = pn.getPlayer();
            var tournament = pn.getTournament();

            log.info("❌ Tournament cancelled: player={}, tournament={}",
                    player.getId(), tournament.getId());

            if (notificationPermissionService.canSendPush(player)) {
                String time = tournament.getTime() != null ? tournament.getTime() : "?";
                String date = tournament.getDate() != null ? tournament.getDate().toString() : "?";
                webPushService.sendToPlayer(
                        player.getId(),
                        "❌ Турнир отменён!",
                        PushMessageBuilder.buildCancelledBody(date, time),
                        "/dashboard"
                );
            }
        }
        log.debug("📩 Cancelled notifications: {}", notifications.size());
    }
}