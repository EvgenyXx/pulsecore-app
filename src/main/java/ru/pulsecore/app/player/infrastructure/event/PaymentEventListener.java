package ru.pulsecore.app.player.infrastructure.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.subscription.SubscriptionCommandService;
import ru.pulsecore.app.shared.event.PaymentSuccessEvent;

/**
 * Обработчик успешного платежа.
 * Активирует подписку игроку после получения PaymentSuccessEvent.
 * todo: заменить на @TransactionalEventListener(AFTER_COMMIT) когда будет сохранение платежа в БД
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {


    private final SubscriptionCommandService subscriptionCommandService;


    // todo: заменить на @TransactionalEventListener(AFTER_COMMIT) когда будет сохранение платежа в БД
    @EventListener
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        subscriptionCommandService.activate(event.playerId(), event.days());
        log.info("Подписка активирована после платежа: playerId={}, days={}",
                event.playerId(), event.days());
    }
}
