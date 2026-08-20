package ru.pulsecore.app.payment.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.payment.api.dto.PaymentResponse;
import ru.pulsecore.app.payment.api.dto.Payment;
import ru.pulsecore.app.payment.infrastructure.properties.YookassaProperties;

import java.util.UUID;

@Slf4j
@Service

public class YookassaService {
    private final YookassaProperties props;
    private final PriceService priceService;
    private final RestTemplate restTemplate = new RestTemplate();

    public YookassaService(YookassaProperties props, PriceService priceService) {
        this.props = props;
        this.priceService = priceService;
    }

    public PaymentResponse createPayment(UUID playerId, int months) {
        int amount = priceService.getPrice(months);
        return Payment.builder()
                .amount(amount)
                .currency(props.getCurrency())
                .returnUrl(props.getReturnUrl())
                .description("Подписка PulseCore на " + months + " мес.")
                .playerId(playerId)
                .months(months)
                .capture(true)
                .shopId(props.getShopId())
                .secretKey(props.getSecretKey())
                .apiUrl(props.getYookassaApiUrl())
                .restTemplate(restTemplate)
                .build()
                .execute();
    }
}