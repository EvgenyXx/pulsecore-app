package ru.pulsecore.app.shop.application.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.application.product.StockService;
import ru.pulsecore.app.shop.domain.OrderStatus;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.domain.PaymentStatus;
import ru.pulsecore.app.shop.domain.entity.Order;
import ru.pulsecore.app.shop.infrastructure.exception.OrderNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.OrderRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCleanupService {

    private static final Duration PAYMENT_TIMEOUT = Duration.ofMinutes(15);

    private final OrderRepository orderRepository;
    private final StockService stockService;

    /**
     * Отменяет неоплаченные заказы ЮKassa, созданные раньше чем PAYMENT_TIMEOUT назад.
     */
    @Transactional
    public void cancelExpiredUnpaidYookassa() {
        LocalDateTime threshold = LocalDateTime.now().minus(PAYMENT_TIMEOUT);

        List<Order> unpaid = orderRepository
                .findByPaymentMethodAndPaymentStatusAndStatusAndCreatedAtBefore(
                        PaymentMethod.YOOKASSA,
                        PaymentStatus.PENDING,
                        OrderStatus.CONFIRMED,
                        threshold
                );

        for (Order order : unpaid) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            stockService.increaseForOrder(order);
        }

        if (!unpaid.isEmpty()) {
            log.info("Отменено неоплаченных заказов ЮKassa: {}", unpaid.size());
        }
    }

     /**
     * Отмена одного заказа. Ставит CANCELLED, возвращает сток.
     * Идемпотентно. Оплаченный заказ не отменяется.
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            log.info("Заказ №{} уже отменён", orderId);
            return;
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            log.warn("Заказ №{} оплачен, отмена игнорируется", orderId);
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        stockService.increaseForOrder(order);

        log.info("Заказ №{} отменён, сток возвращён", orderId);
    }
}