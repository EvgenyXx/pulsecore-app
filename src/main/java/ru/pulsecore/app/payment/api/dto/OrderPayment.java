package ru.pulsecore.app.payment.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Builder
public class OrderPayment {
    private BigDecimal amount;
    private String currency;
    private String returnUrl;
    private String description;
    private Long orderId;
    private boolean capture;
    private int shopId;
    private String secretKey;
    private String apiUrl;
    private RestTemplate restTemplate;

    public PaymentResponse execute() {
        var body = new CreatePaymentRequest(
                new Amount(amount.setScale(2, RoundingMode.HALF_UP).toPlainString(), currency),
                new Confirmation("redirect", returnUrl),
                description,
                new Metadata(orderId.toString()),
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
    private record Metadata(@JsonProperty("orderId") String orderId) {}
}