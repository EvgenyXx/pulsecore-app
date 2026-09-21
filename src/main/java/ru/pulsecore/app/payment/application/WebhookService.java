package ru.pulsecore.app.payment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.payment.api.dto.YookassaWebhook;
import ru.pulsecore.app.shared.event.PaymentSuccessEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final ApplicationEventPublisher publisher;
    private final PaymentService paymentService;

    @Transactional
    public void process(YookassaWebhook webhook) {
        if (!"payment.succeeded".equals(webhook.event())) {
            return;
        }

        var metadata = webhook.object().metadata();
        var amount = webhook.object().amount();
        var playerId = UUID.fromString(metadata.playerId());
        var months = Integer.parseInt(metadata.months());


        paymentService.save(
                playerId,
                new BigDecimal(amount.value()),
                months
        );


        publisher.publishEvent(new PaymentSuccessEvent(
                playerId,
                months * 30,
                amount.value(),
                amount.currency()
        ));

        log.info("Платёж сохранён: playerId={}, months={}, amount={}",
                playerId, months, amount.value());
    }
}