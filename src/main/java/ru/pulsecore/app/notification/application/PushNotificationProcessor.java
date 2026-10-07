package ru.pulsecore.app.notification.application;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.notification.infrastructure.config.NotificationAsyncConfig;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushContent;
import ru.pulsecore.app.shared.event.PushNotificationEvent;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationProcessor {

    private final WebPushService webPushService;
    private final PlayerClient playerClient;

    @Async(NotificationAsyncConfig.PUSH_EXECUTOR)
    public void dispatch(PushNotificationEvent event) {
        Map<UUID, PushContent> contentByPlayer = event.contentByPlayer();

        Set<UUID> allowed = filterAllowed(playerClient.getPlayers(contentByPlayer.keySet()));

        if (allowed.isEmpty()) {
            log.debug("Пуш-рассылка: нет получателей");
            return;
        }
        Map<UUID, PushContent> filtered = contentByPlayer.entrySet().stream()
                .filter(e -> allowed.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        webPushService.sendToPlayers(filtered);

    }


    private Set<UUID> filterAllowed(List<PlayerData> players) {
        return players.stream()
                .filter(playerData -> playerData.pushEnabled() && playerData.hasActiveSubscription())
                .map(PlayerData::id)
                .collect(Collectors.toSet());
    }
}
