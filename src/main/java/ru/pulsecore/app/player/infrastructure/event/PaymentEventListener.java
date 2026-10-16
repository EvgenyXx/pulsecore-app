package ru.pulsecore.app.player.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.subscription.SubscriptionCommandService;
import ru.pulsecore.app.shared.event.SubscriptionWebhookEvent;

/**
 * Обработчик успешного платежа.
 * Активирует подписку игроку после коммита транзакции с платежом.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final SubscriptionCommandService subscriptionCommandService;

    @EventListener
    public void onPaymentSuccess(SubscriptionWebhookEvent event) {
        switch (event.eventType()) {
            case PAYMENT_SUCCEEDED -> {
                subscriptionCommandService.activate(
                        event.playerId(),
                        event.month(),
                        event.amount(),
                        event.paymentId(),
                        event.paidAt());
                log.info("Подписка активирована после платежа: playerId={}, month={}, paymentId={}",
                        event.playerId(), event.month(), event.paymentId());
            }
            case PAYMENT_CANCELED -> log.warn(
                    "Подписка: оплата отменена, playerId={}, paymentId={}",
                    event.playerId(), event.paymentId());
            default -> log.debug("Подписка: пропускаем event={}", event.eventType());
        }
    }
}