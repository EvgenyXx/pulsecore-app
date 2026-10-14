package ru.pulsecore.app.payment.application.handler;

import ru.pulsecore.app.payment.infrastructure.client.YookassaWebhook;
import ru.pulsecore.app.shared.event.YookassaEventType;

public interface WebhookHandler {

    /** Может ли этот handler обработать webhook */
    boolean supports(YookassaWebhook webhook);

    /** Обработать */
    void handle(YookassaWebhook webhook, YookassaEventType eventType);


}