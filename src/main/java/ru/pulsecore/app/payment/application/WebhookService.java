package ru.pulsecore.app.payment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.payment.api.dto.YookassaWebhook;
import ru.pulsecore.app.payment.application.handler.OrderWebhookHandler;
import ru.pulsecore.app.payment.application.handler.SubscriptionWebhookHandler;


@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final OrderWebhookHandler orderWebhookHandler;
    private final SubscriptionWebhookHandler subscriptionWebhookHandler;

    public void process(YookassaWebhook webhook) {
        if (!"payment.succeeded".equals(webhook.event())) {
            return;
        }

        var metadata = webhook.object().metadata();

        if (metadata.orderId() != null) {
            orderWebhookHandler.handle(webhook);
            return;
        }

        if (metadata.playerId() != null) {
            subscriptionWebhookHandler.handle(webhook);
            return;
        }

        log.warn("Webhook без orderId/playerId: {}", webhook);
    }
}