package ru.pulsecore.app.payment.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.payment.infrastructure.client.YookassaWebhook;
import ru.pulsecore.app.shared.event.YookassaEventType;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookDispatcher {

    private final List<WebhookHandler> handlers;

    public void dispatch(YookassaWebhook webhook) {
        YookassaEventType eventType = YookassaEventType.fromCode(webhook.event());

        if (eventType == YookassaEventType.UNKNOWN) {
            log.warn("Неизвестный event: {}", webhook.event());
            return;
        }

        for (WebhookHandler handler : handlers) {
            if (handler.supports(webhook)) {
                handler.handle(webhook, eventType);
                return;
            }
        }

    }
}