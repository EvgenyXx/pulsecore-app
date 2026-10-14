package ru.pulsecore.app.payment.infrastructure.client;

import lombok.Builder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.payment.infrastructure.client.dto.*;
import ru.pulsecore.app.payment.infrastructure.exception.PaymentException;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Builder
public class YookassaSubscriptionPayment {

    private BigDecimal amount;
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
                new Amount(amount.setScale(2, RoundingMode.HALF_UP).toPlainString(), currency),
                new Confirmation("redirect", returnUrl),
                description,
                new SubscriptionMetadata(playerId.toString(), String.valueOf(months)),
                capture
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(String.valueOf(shopId), secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Idempotence-Key", UUID.randomUUID().toString());


        var response = restTemplate.postForEntity(apiUrl, new HttpEntity<>(body, headers),
                CreatePaymentResponse.class);
        var responseBody = response.getBody();
        if (responseBody == null) {
            throw new PaymentException("Юкасса прислала пустой ответ");
        }
        return new PaymentResponse(responseBody.confirmation().confirmationUrl());
    }
}