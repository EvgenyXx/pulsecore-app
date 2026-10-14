package ru.pulsecore.app.payment.infrastructure.client;

import lombok.Builder;
import ru.pulsecore.app.payment.infrastructure.client.dto.*;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;

import java.math.BigDecimal;

@Builder
public class YookassaOrderPayment {

    private final YookassaClient client;

    private BigDecimal amount;
    private String currency;
    private String returnUrl;
    private String description;
    private Long orderId;
    private boolean capture;

    public PaymentResponse execute() {
        var body = new CreatePaymentRequest(
                Amount.of(amount, currency),
                new Confirmation(ConfirmationType.REDIRECT, returnUrl),
                description,
                new OrderMetadata(orderId.toString()),
                capture
        );
        return client.createPayment(body);
    }
}