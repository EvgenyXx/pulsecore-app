package ru.pulsecore.app.payment.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.payment.infrastructure.client.YookassaWebhook;
import ru.pulsecore.app.shared.event.SubscriptionWebhookEvent;
import ru.pulsecore.app.shared.event.YookassaEventType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionWebhookHandler implements WebhookHandler {

    private final ApplicationEventPublisher publisher;

    @Override
    public boolean supports(YookassaWebhook webhook) {
        return webhook.object().metadata().playerId() != null;
    }

    @Override
    public void handle(YookassaWebhook webhook, YookassaEventType eventType) {

        LocalDateTime paidAt = webhook.object().capturedAt() != null
                ? webhook.object().capturedAt().toLocalDateTime()
                : LocalDateTime.now();

        publisher.publishEvent(
                new SubscriptionWebhookEvent(
                        UUID.fromString(webhook.object().metadata().playerId()),
                        eventType,
                        webhook.object().id(),
                        Integer.parseInt(webhook.object().metadata().months()),
                        new BigDecimal(webhook.object().amount().value()),
                        webhook.object().amount().currency(),
                        paidAt
                )
        );
    }
}