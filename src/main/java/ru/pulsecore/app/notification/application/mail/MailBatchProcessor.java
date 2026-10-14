package ru.pulsecore.app.notification.application.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.MailBatchEvent;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.notification.infrastructure.config.NotificationAsyncConfig;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailBatchProcessor {

    private final MailStrategyRegistry mailStrategyRegistry;
    private final PlayerClient playerClient;

    @Async(NotificationAsyncConfig.MAIL_EXECUTOR)
    public void process(MailBatchEvent event) {
        Map<UUID, MailContent> contentByPlayer = event.contentByPlayer();
        if (contentByPlayer == null || contentByPlayer.isEmpty()) return;

        Map<UUID, PlayerData> playerMap = playerClient.getPlayers(contentByPlayer.keySet()).stream()
                .collect(Collectors.toMap(PlayerData::id, p -> p));

        for (Map.Entry<UUID, MailContent> e : contentByPlayer.entrySet()) {
            PlayerData player = playerMap.get(e.getKey());
            if (player == null) continue;

            MailContent content = e.getValue();
            if (!canSend(content.emailType(), player)) continue;

            mailStrategyRegistry.send(content.emailType(), content.context());
        }
    }

    private boolean canSend(MailType type, PlayerData player) {
        if (type.requiresNotificationsEnabled() && !player.notificationsEnabled()) {
            return false;
        }
        return !type.requiresActiveSubscription() || player.hasActiveSubscription();
    }
}