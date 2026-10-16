package ru.pulsecore.app.payment.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.payment.infrastructure.client.dto.CreatePaymentRequest;
import ru.pulsecore.app.payment.infrastructure.client.dto.CreatePaymentResponse;
import ru.pulsecore.app.payment.infrastructure.exception.PaymentException;
import ru.pulsecore.app.payment.infrastructure.properties.YookassaProperties;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class YookassaClient {

    private final RestTemplate yookassaRestTemplate;
    private final YookassaProperties props;

    public PaymentResponse createPayment(CreatePaymentRequest body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(String.valueOf(props.getShopId()), props.getSecretKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Idempotence-Key", UUID.randomUUID().toString());

        var response = yookassaRestTemplate.postForEntity(
                props.getYookassaApiUrl(),
                new HttpEntity<>(body, headers),
                CreatePaymentResponse.class
        );

        var responseBody = response.getBody();
        if (responseBody == null || responseBody.confirmation() == null) {
            throw new PaymentException("Юкасса прислала пустой ответ");
        }

        return new PaymentResponse(responseBody.confirmation().confirmationUrl());
    }
}