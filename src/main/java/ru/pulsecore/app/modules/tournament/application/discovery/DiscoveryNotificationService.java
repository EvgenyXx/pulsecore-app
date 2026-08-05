package ru.pulsecore.app.modules.tournament.application.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.notification.application.WebPushService;
import ru.pulsecore.app.modules.notification.application.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.notification.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification.application.mail.context.NewTournamentContext;
import ru.pulsecore.app.modules.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.modules.tournament.infrastructure.util.StringUtils;
import ru.pulsecore.app.modules.shared.util.PushMessageBuilder;
import ru.pulsecore.app.modules.tournament.application.notification.NotificationPermissionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscoveryNotificationService {

    //todo кафка мини
    private final MailStrategyRegistry mailStrategyRegistry;
    private final WebPushService webPushService;
    private final NotificationPermissionService notificationPermissionService;

    public void sendNotifications(UUID playerId,
                                  String email,
                                  String name,
                                  List<TournamentDto> tournaments) {
        sendEmailNotifications(playerId,email,name, tournaments);
        sendPushNotifications(playerId,email,name, tournaments);
    }

    private void sendEmailNotifications(
            UUID playerId, String email,String name,
            List<TournamentDto> tournaments) {
        if (!notificationPermissionService.canSendEmail(playerId)) {
            log.info("🔕 Email notifications disabled for {}", email);
            return;
        }
        tournaments.forEach(t -> {
            String firstName = StringUtils.extractFirstName(name);
            String rawDate = t.getDate() != null ? t.getDate().getDate() : null;
            mailStrategyRegistry.send(MailTypes.NEW_TOURNAMENT,
                    new NewTournamentContext(
                            email,
                            firstName,
                            DateTimeUtils.formatDate(rawDate),
                            DateTimeUtils.formatTime(rawDate),
                            t.getHall() != null ? t.getHall() : "—",
                            t.getLeague() != null ? t.getLeague() : "—",
                            t.getPlayers() != null && !t.getPlayers().isEmpty()
                                    ? String.join("\n", t.getPlayers()) : "—",
                            t.getLink() != null ? t.getLink() : ""));
        });
    }

    private void sendPushNotifications(UUID playerId,
                                       String email,
                                       String name,
                                       List<TournamentDto> tournaments) {
        if (!notificationPermissionService.canSendPush(playerId)) {
            log.info("🔕 Push notifications disabled for {}", email);
            return;
        }
        tournaments.forEach(t -> webPushService.sendToPlayer(
                playerId,
                "📋 Вы в составе!",
                PushMessageBuilder.buildNewTournamentBody(name, t),
                "/dashboard"
        ));
    }
}