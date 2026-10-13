package ru.pulsecore.app.payment.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.payment.api.dto.YookassaWebhook;
import ru.pulsecore.app.shared.event.OrderWebhookEvent;
import ru.pulsecore.app.shared.event.YookassaEventType;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderWebhookHandler implements WebhookHandler {

    private final ApplicationEventPublisher publisher;

    @Override
    public boolean supports(YookassaWebhook webhook) {
        return webhook.object().metadata().orderId() != null;
    }

    @Override
    public void handle(YookassaWebhook webhook, YookassaEventType eventType) {
        var metadata = webhook.object().metadata();
        var amount = webhook.object().amount();

        publisher.publishEvent(new OrderWebhookEvent(
                Long.parseLong(metadata.orderId()),
                eventType,
                new java.math.BigDecimal(amount.value()),
                amount.currency()
        ));
    }
}