package ru.pulsecore.app.shop.application.payment;

import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.domain.Order;
import ru.pulsecore.app.shop.domain.PaymentMethod;

public interface OrderPaymentStrategy {
    PaymentMethod supportedMethod();
    OrderDto handlePayment(Order order);
}