package ru.pulsecore.app.payment.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;


@JsonIgnoreProperties(ignoreUnknown = true)
public record YookassaWebhook(
        String type,
        String event,
        PaymentObject object
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentObject(
            String id,
            String status,
            Amount amount,
            Metadata metadata,
            @JsonProperty("payment_method") PaymentMethod paymentMethod,
            @JsonProperty("captured_at") OffsetDateTime capturedAt     // ← ДОБАВИТЬ
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentMethod(String type) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Amount(String value, String currency) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(
            @JsonProperty("playerId") String playerId,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("months") String months
    ) {
    }
}