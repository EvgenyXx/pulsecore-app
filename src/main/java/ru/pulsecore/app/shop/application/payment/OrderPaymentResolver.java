package ru.pulsecore.app.shop.application.payment;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.domain.Order;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.infrastructure.exception.OrderException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderPaymentResolver {

    private final Map<PaymentMethod, OrderPaymentStrategy> strategyMap;

    public OrderPaymentResolver(List<OrderPaymentStrategy> strategyList) {
        this.strategyMap = strategyList.stream()
                .collect(Collectors.toMap(
                        OrderPaymentStrategy::supportedMethod,
                        s -> s));
    }


    public OrderDto pay(Order order){
        OrderPaymentStrategy paymentStrategy = strategyMap.get(order.getPaymentMethod());
        if (paymentStrategy == null){
            throw new OrderException("Такой способ оплаты отсутсвтует: " + order.getPaymentMethod());
        }
        return paymentStrategy.handlePayment(order);
    }


}
