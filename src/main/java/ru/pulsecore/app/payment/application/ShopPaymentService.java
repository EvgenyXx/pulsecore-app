package ru.pulsecore.app.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.payment.infrastructure.client.YookassaClient;
import ru.pulsecore.app.payment.infrastructure.client.YookassaOrderPayment;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;
import ru.pulsecore.app.payment.infrastructure.properties.YookassaProperties;
import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class ShopPaymentService {

    private final YookassaProperties props;
    private final YookassaClient yookassaClient;



    public PaymentResponse createOrderPayment(
            Long orderId,
            BigDecimal amount) {
        return YookassaOrderPayment.builder()
                .client(yookassaClient)
                .amount(amount)
                .currency(props.getCurrency())
                .returnUrl(props.getOrderReturnUrl())
                .description("Заказ №" + orderId)
                .orderId(orderId)
                .capture(true)
                .build()
                .execute();
    }

}