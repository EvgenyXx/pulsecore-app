package ru.pulsecore.app.player.application.subscription;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.player.infrastructure.repository.SubscriptionRepository;
import ru.pulsecore.app.shared.dispetcher.PushDispatcher;
import ru.pulsecore.app.shared.event.PushContent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import java.time.LocalDate;
import java.util.*;

/**
 * Обработчик просроченных и истекающих подписок.
 * Деактивирует подписки с истекшим сроком.
 * Отправляет push-уведомления игрокам, у которых подписка истекает завтра.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionExpiryProcessor {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionQueryService subscriptionQueryService;
    private final PushDispatcher pushDispatcher;

    @Transactional
    public void processDeactivatedSubscription() {
        List<Subscription> expired = subscriptionQueryService.findExpired();
        List<Subscription> save = new ArrayList<>();
        for (Subscription sub : expired) {
            sub.setActive(false);
            save.add(sub);
        }

        subscriptionRepository.saveAll(save);
        log.info("❌ Деактивировано {} просроченных подписок", expired.size());
        if (!expired.isEmpty()) {
            log.info("Деактивировано {} просроченных подписок", expired.size());
        }
    }

    public void processCheckingSubscription() {
        Set<UUID> expiringIds = subscriptionQueryService.
                findExpiringPlayerIds(LocalDate.now().plusDays(1));
        if (expiringIds.isEmpty()) return;

        pushDispatcher.send(PushContent.sameForAll(
                expiringIds, "Подписка скоро закончится",
                PushMessageBuilder.SUBSCRIPTION_EXPIRING_BODY,
                "/dashboard#/profile"));

    }
}


