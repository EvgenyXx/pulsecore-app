package ru.pulsecore.app.shop.application.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.domain.OrderStatus;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.domain.PaymentStatus;
import ru.pulsecore.app.shop.infrastructure.repository.OrderRepository;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCleanupService {

    private static final Duration PAYMENT_TIMEOUT = Duration.ofMinutes(15);

    private final OrderRepository orderRepository;

    /**
     * Отменяет неоплаченные заказы ЮKassa, созданные раньше чем PAYMENT_TIMEOUT назад.
     */
    @Transactional
    public void cancelExpiredUnpaidYookassa() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minus(PAYMENT_TIMEOUT);

        int cancelled = orderRepository.cancelUnpaidBefore(
                PaymentMethod.YOOKASSA,
                PaymentStatus.PENDING,
                OrderStatus.CONFIRMED,
                threshold,
                OrderStatus.CANCELLED,
                now
        );

        if (cancelled > 0) {
            log.info("Отменено неоплаченных заказов ЮKassa: {}", cancelled);
        }
    }
}