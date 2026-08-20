package ru.pulsecore.app.payment.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Builder
public class Payment {
    private int amount;
    private String currency;
    private String returnUrl;
    private String description;
    private UUID playerId;
    private int months;
    private boolean capture;
    private int shopId;
    private String secretKey;
    private String apiUrl;
    private RestTemplate restTemplate;

    public PaymentResponse execute() {
        var body = new CreatePaymentRequest(
                new Amount(amount + ".00", currency),
                new Confirmation("redirect", returnUrl),
                description,
                new Metadata(playerId.toString(), String.valueOf(months)),
                capture
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(String.valueOf(shopId), secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Idempotence-Key", UUID.randomUUID().toString());

        var response = restTemplate.postForEntity(apiUrl, new HttpEntity<>(body, headers), Map.class);
        var responseBody = response.getBody();
        var confirmation = (Map<String, Object>) responseBody.get("confirmation");
        return new PaymentResponse((String) confirmation.get("confirmation_url"));
    }

    private record CreatePaymentRequest(Amount amount, Confirmation confirmation, String description, Metadata metadata, boolean capture) {}
    private record Amount(@JsonProperty("value") String value, @JsonProperty("currency") String currency) {}
    private record Confirmation(@JsonProperty("type") String type, @JsonProperty("return_url") String returnUrl) {}
    private record Metadata(@JsonProperty("playerId") String playerId, @JsonProperty("months") String months) {}
}