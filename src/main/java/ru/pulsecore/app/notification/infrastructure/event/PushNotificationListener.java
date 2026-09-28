package ru.pulsecore.app.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.WebPushService;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushNotificationEvent;

import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificationListener {

    private final WebPushService webPushService;
    private final PlayerClient playerClient;


    @EventListener
    public void sendPush(PushNotificationEvent event) {
        if (!canSendPush(event.playerId())) {
            log.debug("Пуш отключены для пользователя {}", event.playerId());
            return;
        }

        webPushService.sendToPlayer(
                event.playerId(),
                event.title(),
                event.body(), event.url()
        );

    }

    private boolean canSendPush(UUID playerId) {
        PlayerData playerData = playerClient.getPlayer(playerId);

        return playerData.pushEnabled() && playerData.hasActiveSubscription();
    }
}
