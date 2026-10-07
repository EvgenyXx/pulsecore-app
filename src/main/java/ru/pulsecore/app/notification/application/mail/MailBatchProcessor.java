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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailBatchProcessor {

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

    @Async(NotificationAsyncConfig.MAIL_EXECUTOR)
    public void process(MailBatchEvent event) {
        Map<UUID, MailContent> contentByPlayer = event.contentByPlayer();
        if (contentByPlayer == null || contentByPlayer.isEmpty()) return;

        // 1 HTTP — все игроки
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

    private boolean canSend(String emailType, PlayerData player) {
        if (!CHECK_NOTIFICATIONS_TYPES.contains(emailType)) return true;
        return player.notificationsEnabled() && player.hasActiveSubscription();
    }
}