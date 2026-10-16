package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Confirmation(
        @JsonProperty("type") ConfirmationType type,
        @JsonProperty("return_url") String returnUrl
) {}