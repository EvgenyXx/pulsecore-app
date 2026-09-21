package ru.pulsecore.app.player.infrastructure.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.pulsecore.app.player.application.subscription.SubscriptionCommandService;
import ru.pulsecore.app.shared.event.PaymentSuccessEvent;

/**
 * Обработчик успешного платежа.
 * Активирует подписку игроку после коммита транзакции с платежом.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final SubscriptionCommandService subscriptionCommandService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        subscriptionCommandService.activate(event.playerId(), event.days());
        log.info("Подписка активирована после платежа: playerId={}, days={}",
                event.playerId(), event.days());
    }
}