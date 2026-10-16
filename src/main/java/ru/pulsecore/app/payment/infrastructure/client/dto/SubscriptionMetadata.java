package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionMetadata(
        @JsonProperty("playerId") String playerId,
        @JsonProperty("months") String months
) implements PaymentMetadata {}