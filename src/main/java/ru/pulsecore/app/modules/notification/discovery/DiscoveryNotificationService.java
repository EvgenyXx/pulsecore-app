package ru.pulsecore.app.modules.notification.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.notification.service.NotificationPermissionService;
import ru.pulsecore.app.modules.shared.util.push.PushMessageBuilder;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.push.service.WebPushService;
import ru.pulsecore.app.modules.shared.service.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.shared.service.mail.MailTypes;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscoveryNotificationService {

    private final MailStrategyRegistry mailStrategyRegistry;
    private final WebPushService webPushService;
    private final NotificationPermissionService notificationPermissionService;

    public void sendNotifications(Player user, List<TournamentDto> tournaments) {
        sendEmailNotifications(user, tournaments);
        sendPushNotifications(user, tournaments);
    }

    private void sendEmailNotifications(Player user, List<TournamentDto> tournaments) {
        if (!notificationPermissionService.canSendEmail(user)) {
            log.info("🔕 Email notifications disabled for {}", user.getEmail());
            return;
        }
        tournaments.forEach(t -> mailStrategyRegistry.send(MailTypes.NEW_TOURNAMENT, user.getEmail(), t, user));
    }

    private void sendPushNotifications(Player user, List<TournamentDto> tournaments) {
        if (!notificationPermissionService.canSendPush(user)) {
            log.info("🔕 Push notifications disabled for {}", user.getEmail());
            return;
        }
        tournaments.forEach(t -> webPushService.sendToPlayer(
                user.getId(),
                "📋 Вы в составе!",
                PushMessageBuilder.buildNewTournamentBody(user.getName(), t),
                "/dashboard"
        ));
    }
}