package ru.pulsecore.app.payment.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


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
            @JsonProperty("payment_method") PaymentMethod paymentMethod
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentMethod(String type) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Amount(String value, String currency) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(
            @JsonProperty("id") String playerId,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("months") String months
    ) {}
}