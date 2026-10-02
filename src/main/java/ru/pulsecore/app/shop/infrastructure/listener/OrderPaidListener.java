package ru.pulsecore.app.shop.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.OrderPaidEvent;
import ru.pulsecore.app.shop.application.order.OrderPaidService;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaidListener {

    private final OrderPaidService orderPaidService;

    @EventListener
    @Transactional
    public void onOrderPaid(OrderPaidEvent event) {
        orderPaidService.markPaid(event.orderId());
        log.info("Shop: заказ {} помечен оплаченным", event.orderId());
    }
}
