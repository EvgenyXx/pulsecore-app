package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Amount(
        @JsonProperty("value") String value,
        @JsonProperty("currency") String currency
) {
    public static Amount of(BigDecimal amount, String currency) {
        return new Amount(
                amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                currency
        );
    }
}