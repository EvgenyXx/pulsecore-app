package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePaymentRequest(
        @JsonProperty("amount") Amount amount,
        @JsonProperty("confirmation") Confirmation confirmation,
        @JsonProperty("description") String description,
        @JsonProperty("metadata") PaymentMetadata metadata,
        @JsonProperty("capture") boolean capture
) {}