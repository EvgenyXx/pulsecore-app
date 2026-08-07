package ru.pulsecore.app.player.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.subscription.SubscriptionExpiryProcessor;


@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpiryScheduler {


    private final SubscriptionExpiryProcessor expiryProcessor;


    @Scheduled(cron = "0 0 * * * *")
    public void deactivateExpired() {
       expiryProcessor.processDeactivatedSubscription();
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void checkExpiringSubscriptions() {
       expiryProcessor.processCheckingSubscription();
    }
}