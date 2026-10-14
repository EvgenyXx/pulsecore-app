package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionMetadata(
        @JsonProperty("id") String playerId,
        @JsonProperty("month") String months
) {}