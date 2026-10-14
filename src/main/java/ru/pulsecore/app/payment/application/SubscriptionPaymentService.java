package ru.pulsecore.app.payment.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.payment.application.price.PriceService;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;
import ru.pulsecore.app.payment.infrastructure.client.YookassaSubscriptionPayment;
import ru.pulsecore.app.payment.infrastructure.properties.YookassaProperties;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class SubscriptionPaymentService {

    private final YookassaProperties props;
    private final PriceService priceService;
    private final RestTemplate restTemplate = new RestTemplate();

    public SubscriptionPaymentService(YookassaProperties props, PriceService priceService) {
        this.props = props;
        this.priceService = priceService;
    }

    public PaymentResponse createPayment(UUID playerId, int months) {
        BigDecimal amount = BigDecimal.valueOf(priceService.getPrice(months));
        return YookassaSubscriptionPayment.builder()
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