package ru.pulsecore.app.player.application.subscription;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Обработчик просроченных и истекающих подписок.
 * Деактивирует подписки с истекшим сроком.
 * Отправляет push-уведомления игрокам, у которых подписка истекает завтра.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionExpiryProcessor {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final ApplicationEventPublisher eventPublisher;
    private final PlayerSearchService playerSearchService;

    @Transactional
    public void processDeactivatedSubscription() {
        List<Subscription> expired = subscriptionQueryService.findExpired();
        for (Subscription sub : expired) {
            sub.setActive(false);
            subscriptionCommandService.save(sub);
            log.info("❌ Подписка истекла: {}", sub.getPlayer().getEmail());
        }
        if (!expired.isEmpty()) {
            log.info("🔧 Деактивировано {} просроченных подписок", expired.size());
        }
    }

    public void processCheckingSubscription() {
        Set<UUID> expiringIds = subscriptionQueryService.findExpiringPlayerIds(LocalDate.now().plusDays(1));
        if (expiringIds.isEmpty()) return;

        List<PlayerData> players = playerSearchService.findPlayerByIds(expiringIds);

        for (PlayerData player : players) {
            if (!player.pushEnabled()) continue;

            eventPublisher.publishEvent(
                    new PushNotificationEvent(
                            player.playerId(),
                            "⏳ Подписка заканчивается!",
                            PushMessageBuilder.SUBSCRIPTION_EXPIRING_BODY,
                            "/subscribe"
                    )
            );
            log.info("📲 Push отправлен игроку {}", player.playerId());
        }
    }
}


