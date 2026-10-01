package ru.pulsecore.app.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.payment.api.dto.OrderPayment;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;
import ru.pulsecore.app.payment.infrastructure.properties.YookassaProperties;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class ShopPaymentService {

    private final YookassaProperties props;
    private final RestTemplate restTemplate = new RestTemplate();


    public PaymentResponse createOrderPayment(
            Long orderId,
            BigDecimal amount) {
        return OrderPayment.builder()
                .amount(amount)
                .currency(props.getCurrency())
                .returnUrl(props.getOrderReturnUrl())
                .description("Заказ №" + orderId)
                .orderId(orderId)
                .capture(true)
                .shopId(props.getShopId())
                .secretKey(props.getSecretKey())
                .apiUrl(props.getYookassaApiUrl())
                .restTemplate(restTemplate)
                .build()
                .execute();
    }

}
