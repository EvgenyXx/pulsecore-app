package ru.pulsecore.app.shop.infrastructure.client;

import ru.pulsecore.app.shared.dto.response.PaymentResponse;

import java.math.BigDecimal;

public interface PaymentShopClient {

    PaymentResponse createOrderPayment(Long orderId,
                                       BigDecimal amount);
}
