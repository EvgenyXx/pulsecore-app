package ru.pulsecore.app.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.PaymentNotificationService;
import ru.pulsecore.app.shared.event.SubscriptionWebhookEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentNotificationListener {

    private final PaymentNotificationService notificationService;

    @EventListener
    public void onPaymentEvent(SubscriptionWebhookEvent event) {
        log.debug("Событие оплаты: playerId={}, type={}",
                event.playerId(), event.eventType());

        switch (event.eventType()) {
            case PAYMENT_SUCCEEDED -> notificationService.sendSuccess(event);
            case PAYMENT_CANCELED -> log.warn("Оплата отменена: playerId={}, paymentId={}",
                    event.playerId(), event.paymentId());
            default -> log.debug("Пропускаем событие: {}", event.eventType());
        }
    }
}