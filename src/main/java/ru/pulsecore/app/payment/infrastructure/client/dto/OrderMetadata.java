package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderMetadata(
        @JsonProperty("orderId") String orderId
) implements PaymentMetadata {}