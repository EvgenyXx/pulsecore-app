package ru.pulsecore.app.payment.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.payment.api.dto.YookassaWebhook;
import ru.pulsecore.app.payment.application.PaymentService;
import ru.pulsecore.app.shared.event.PaymentSuccessEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionWebhookHandler {

    private final ApplicationEventPublisher publisher;
    private final PaymentService paymentService;

    @Transactional
    public void handle(YookassaWebhook webhook) {
        var metadata = webhook.object().metadata();
        var amount = webhook.object().amount();

        UUID playerId = UUID.fromString(metadata.playerId());
        int months = Integer.parseInt(metadata.months());

        paymentService.save(playerId, new BigDecimal(amount.value()), months);

        publisher.publishEvent(new PaymentSuccessEvent(
                playerId,
                months * 30,
                amount.value(),
                amount.currency()
        ));

        log.info("Подписка оплачена: id={}, months={}, amount={}",
                playerId, months, amount.value());
    }
}