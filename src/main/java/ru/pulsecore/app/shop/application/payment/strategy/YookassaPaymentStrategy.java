package ru.pulsecore.app.shop.application.payment.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.application.mapping.OrderMapper;
import ru.pulsecore.app.shop.application.payment.OrderPaymentStrategy;
import ru.pulsecore.app.shop.domain.entity.Order;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.infrastructure.client.PaymentShopClient;
import ru.pulsecore.app.shop.infrastructure.config.ShopProperties;


@Component
@RequiredArgsConstructor
public class YookassaPaymentStrategy implements OrderPaymentStrategy {

    private final PaymentShopClient paymentShopClient;
    private final OrderMapper orderMapper;
    private final ShopProperties shopProperties;

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.YOOKASSA;
    }

    @Override
    public OrderDto handlePayment(Order order) {
        PaymentResponse payment = paymentShopClient.createOrderPayment(
                order.getId(),
                order.getTotalPrice()
        );
        return orderMapper.toDto(order, payment.confirmationUrl(), shopProperties);
    }
}