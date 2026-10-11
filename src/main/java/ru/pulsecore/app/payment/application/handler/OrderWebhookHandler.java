package ru.pulsecore.app.payment.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.payment.api.dto.YookassaWebhook;
import ru.pulsecore.app.shared.dto.response.OrderYookassaEvent;
import ru.pulsecore.app.shared.event.YookassaEventType;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderWebhookHandler {

    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void handle(YookassaWebhook webhook) {
        Long orderId = Long.parseLong(webhook.object().metadata().orderId());
        YookassaEventType eventType = YookassaEventType.fromCode(webhook.event());
        if (eventType == null) {
            log.warn("Неизвестный event от ЮKassa: {}", webhook.event());
            return;
        }

        eventPublisher.publishEvent(new OrderYookassaEvent(
                orderId,
                eventType,
                webhook.object().id()
        ));
        log.info("Заказ оплачен: id={}", orderId);
    }
}