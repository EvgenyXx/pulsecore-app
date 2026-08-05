package ru.pulsecore.app.modules.player.infrastructure.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.pulsecore.app.modules.player.application.subscription.SubscriptionService;
import ru.pulsecore.app.modules.shared.dto.PaymentSuccessEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {


    private final SubscriptionService subscriptionService;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        subscriptionService.activate(event.playerId(), event.days());
        log.info("Подписка активирована после платежа: playerId={}, days={}",
                event.playerId(), event.days());
    }
}
