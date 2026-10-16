package ru.pulsecore.app.payment.infrastructure.client;

import lombok.Builder;
import ru.pulsecore.app.payment.infrastructure.client.dto.*;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public class YookassaSubscriptionPayment {

    private final YookassaClient client;

    private BigDecimal amount;
    private String currency;
    private String returnUrl;
    private String description;
    private UUID playerId;
    private int months;
    private boolean capture;

    public PaymentResponse execute() {
        var body = new CreatePaymentRequest(
                Amount.of(amount, currency),
                new Confirmation(ConfirmationType.REDIRECT, returnUrl),
                description,
                new SubscriptionMetadata(playerId.toString(), String.valueOf(months)),
                capture
        );
        return client.createPayment(body);
    }
}