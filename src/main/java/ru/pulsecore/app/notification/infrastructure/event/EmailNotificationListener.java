package ru.pulsecore.app.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategyRegistry;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.MailNotificationEvent;

import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final MailStrategyRegistry mailStrategyRegistry;
    private final PlayerClient playerClient;


    private static final Set<String> CHECK_NOTIFICATIONS_TYPES = Set.of(
        MailTypes.NEW_TOURNAMENT,
        MailTypes.CANCELED_TOURNAMENT,
        MailTypes.PLAYER_REPLACED,
        MailTypes.PLAYER_TRANSFERRED,
        MailTypes.TOURNAMENT_SCHEDULE_CHANGED,
        MailTypes.SCHEDULED_REPORT
);

    @EventListener
    public void handle(MailNotificationEvent event) {
        if (!canSend(event)){
            return;
        }
        mailStrategyRegistry.send(event.getEmailType(), event.getContextMessage());

    }

    private boolean canSend(MailNotificationEvent event) {
    if (!CHECK_NOTIFICATIONS_TYPES.contains(event.getEmailType())) {
        return true;
    }
    PlayerData player = playerClient.getPlayer(event.getPlayerId());
    return player.notificationsEnabled() && player.hasActiveSubscription();
}

}