package ru.pulsecore.app.player.application.subscription;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.application.player.PlayerService;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.player.infrastructure.persistence.repository.SubscriptionRepository;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionExpiryProcessor {

    private final SubscriptionRepository subscriptionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PlayerService playerService;

    public void processDeactivatedSubscription() {
        List<Subscription> expired = subscriptionRepository.findExpired();
        for (Subscription sub : expired) {
            sub.setActive(false);
            subscriptionRepository.save(sub);
            log.info("❌ Подписка истекла: {}", sub.getPlayer().getEmail());
        }
        if (!expired.isEmpty()) {
            log.info("🔧 Деактивировано {} просроченных подписок", expired.size());
        }
    }

    public void processCheckingSubscription() {
        Set<UUID> expiringIds = subscriptionRepository.findExpiringPlayerIds();
        if (expiringIds.isEmpty()) return;

        List<PlayerData> players = playerService.findPlayerByIds(expiringIds);

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


