package ru.pulsecore.app.shop.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shop.application.order.OrderCleanupService;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnpaidYookassaScheduler {

    private final OrderCleanupService orderCleanupService;

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void run() {
       orderCleanupService.cancelExpiredUnpaidYookassa();
    }
}