package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Confirmation(
        @JsonProperty("type") String type,
        @JsonProperty("return_url") String returnUrl
) {}