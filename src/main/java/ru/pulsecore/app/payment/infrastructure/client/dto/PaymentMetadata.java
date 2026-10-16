package ru.pulsecore.app.payment.infrastructure.client.dto;

public sealed interface PaymentMetadata
        permits SubscriptionMetadata, OrderMetadata {
}