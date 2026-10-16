package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreatePaymentResponse(
        String id,
        String status,
        Boolean paid,
        Amount amount,
        Confirmation confirmation,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        String description,
        Map<String, String> metadata,
        @JsonProperty("payment_method") PaymentMethod paymentMethod,
        Recipient recipient,
        Boolean refundable,
        Boolean test
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Amount(String value, String currency) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Confirmation(
            String type,
            @JsonProperty("return_url") String returnUrl,
            @JsonProperty("confirmation_url") String confirmationUrl
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentMethod(
            String type,
            String id,
            Boolean saved
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Recipient(
            @JsonProperty("account_id") String accountId,
            @JsonProperty("gateway_id") String gatewayId
    ) {
    }
}