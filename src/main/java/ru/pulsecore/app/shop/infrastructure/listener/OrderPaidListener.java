package ru.pulsecore.app.shop.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.OrderYookassaEvent;
import ru.pulsecore.app.shop.application.order.OrderCleanupService;
import ru.pulsecore.app.shop.application.order.OrderPaidService;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaidListener {

    private final OrderPaidService orderPaidService;
    private final OrderCleanupService orderCleanupService;

    @EventListener
    @Transactional
    public void onOrderPaid(OrderYookassaEvent orderYookassaEvent) {
        switch (orderYookassaEvent.eventType()) {
            case PAYMENT_SUCCEEDED -> orderPaidService.markPaid(orderYookassaEvent.orderId());
            case PAYMENT_CANCELED -> orderCleanupService.cancelOrder(orderYookassaEvent.orderId());
            default -> log.warn("Необработанный event: {}", orderYookassaEvent.eventType());
        }
    }
}


//orderPaidService.markPaid(orderPaidEvent.orderId());
//        log.info("Shop: заказ {} помечен оплаченным", orderPaidEvent.orderId());