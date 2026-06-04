package ru.pulsecore.app.modules.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.domain.Subscription;
import ru.pulsecore.app.modules.player.repository.SubscriptionRepository;
import ru.pulsecore.app.modules.push.service.WebPushService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpiryScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final WebPushService webPushService;

    // Каждый час деактивируем просроченные
    @Scheduled(cron = "0 0 * * * *")
    public void deactivateExpired() {
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

    @Scheduled(cron = "0 0 10 * * *")
    public void checkExpiringSubscriptions() {
        List<Subscription> expiringTomorrow = subscriptionRepository.findExpiringTomorrow();

        for (Subscription sub : expiringTomorrow) {
            Player player = sub.getPlayer();
            if (player == null || !player.isPushEnabled()) continue;

            webPushService.sendToPlayer(
                    player.getId(),
                    "⏳ Подписка заканчивается!",
                    "Завтра истекает срок действия подписки.\n\n🔕 Push-уведомления будут отключены.\n💳 Продлите подписку, чтобы продолжить получать уведомления о турнирах.\n\nPulseCore",
                    "/subscribe"
            );
            log.info("📲 Subscription expiry push sent to {}", player.getEmail());
        }
    }
}