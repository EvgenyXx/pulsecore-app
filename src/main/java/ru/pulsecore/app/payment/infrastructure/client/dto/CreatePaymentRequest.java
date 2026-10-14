package ru.pulsecore.app.payment.infrastructure.client.dto;

public record CreatePaymentRequest(
        Amount amount,
        Confirmation confirmation,
        String description,
        SubscriptionMetadata metadata,
        boolean capture
) {}