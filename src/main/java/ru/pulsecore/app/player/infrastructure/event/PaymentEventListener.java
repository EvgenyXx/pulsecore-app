package ru.pulsecore.app.player.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.subscription.SubscriptionCommandService;
import ru.pulsecore.app.shared.event.SubscriptionActivatedEvent;

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
    public void onPaymentSuccess(SubscriptionActivatedEvent event) {
        subscriptionCommandService.activate(event.playerId(), event.days());
        log.info("Подписка активирована после платежа: id={}, days={}",
                event.playerId(), event.days());
    }
}