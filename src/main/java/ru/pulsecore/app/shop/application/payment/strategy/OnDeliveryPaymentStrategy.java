package ru.pulsecore.app.shop.application.payment.strategy;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.application.mapping.OrderMapper;
import ru.pulsecore.app.shop.application.payment.OrderPaymentStrategy;
import ru.pulsecore.app.shop.domain.entity.Order;
import ru.pulsecore.app.shop.domain.PaymentMethod;

@Component
@RequiredArgsConstructor
public class OnDeliveryPaymentStrategy implements OrderPaymentStrategy {

    private final OrderMapper orderMapper;


    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.ON_DELIVERY;
    }

    @Override
    public OrderDto handlePayment(Order order) {
        return orderMapper.toDto(order);
    }
}
