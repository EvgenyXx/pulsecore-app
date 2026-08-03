package ru.pulsecore.app.modules.player_modeles.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player_modeles.entity.Player;
import ru.pulsecore.app.modules.player_modeles.entity.Subscription;
import ru.pulsecore.app.modules.player_modeles.infrastructure.persistence.repository.SubscriptionRepository;
import ru.pulsecore.app.modules.notification_modules.application.WebPushService;
import ru.pulsecore.app.modules.shared.util.push.PushMessageBuilder;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpiryScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final WebPushService webPushService;//todo через мини кафку сделать


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
                    PushMessageBuilder.SUBSCRIPTION_EXPIRING_BODY,
                    "/subscribe"
            );
            log.info("📲 Subscription expiry push sent to {}", player.getEmail());
        }
    }
}