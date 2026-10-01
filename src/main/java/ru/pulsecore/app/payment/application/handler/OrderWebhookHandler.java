package ru.pulsecore.app.payment.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.payment.api.dto.YookassaWebhook;
import ru.pulsecore.app.shared.dto.response.OrderPaidEvent;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderWebhookHandler {

    private final ApplicationEventPublisher eventPublisher;

    @Transactional //todo добавить сохранение в систему
    public void handle(YookassaWebhook webhook) {
        Long orderId = Long.parseLong(webhook.object().metadata().orderId());
        eventPublisher.publishEvent(new OrderPaidEvent(orderId));
        log.info("Заказ оплачен: id={}", orderId);
    }
}