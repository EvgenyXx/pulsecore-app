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