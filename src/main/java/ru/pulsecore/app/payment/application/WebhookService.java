package ru.pulsecore.app.payment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.admin.AdminPaymentContext;
import ru.pulsecore.app.payment.api.dto.YookassaWebhook;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.shared.event.PaymentSuccessEvent;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final ApplicationEventPublisher publisher;


    public void process(YookassaWebhook webhook) {
        if (!"payment.succeeded".equals(webhook.event())) {
            return;
        }


        var metadata = webhook.object().metadata();
        var amount = webhook.object().amount();
        var playerId = UUID.fromString(metadata.playerId());
        var months = Integer.parseInt(metadata.months());

        publisher.publishEvent(new PaymentSuccessEvent(playerId, months * 30));
        log.info("Subscription activated: playerId={}, months={}", playerId, months);

        publisher.publishEvent(new MailNotificationEvent(
                MailTypes.ADMIN_PAYMENT_RECEIVED,
                new AdminPaymentContext(playerId.toString(), months, amount.value(), amount.currency())));
    }


}