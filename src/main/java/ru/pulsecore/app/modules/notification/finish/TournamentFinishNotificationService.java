package ru.pulsecore.app.modules.notification.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.notification.service.NotificationPermissionService;
import ru.pulsecore.app.modules.push.service.WebPushService;

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

            // Отправляем push, если разрешено
            if (notificationPermissionService.canSendPush(player)) {
                String time = tournament.getTime() != null ? tournament.getTime() : "?";
                webPushService.sendToPlayer(
                        player.getId(),
                        "❌ Турнир отменён!",
                        "Турнир " + tournament.getDate() + " в " + time + " был отменён.\n\nPulseCore",
                        "/dashboard"
                );
            }
        }
        log.info("📩 Cancelled notifications: {}", notifications.size());
    }
}