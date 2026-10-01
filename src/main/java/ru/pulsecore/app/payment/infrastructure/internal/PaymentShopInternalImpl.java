package ru.pulsecore.app.payment.infrastructure.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.payment.application.ShopPaymentService;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;
import ru.pulsecore.app.shop.infrastructure.client.PaymentShopClient;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentShopInternalImpl implements PaymentShopClient {

    private final ShopPaymentService shopPaymentService;

    @Override
    public PaymentResponse createOrderPayment(Long orderId, BigDecimal amount) {
        return shopPaymentService.createOrderPayment(orderId, amount);
    }
}
