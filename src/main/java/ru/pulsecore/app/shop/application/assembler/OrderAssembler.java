package ru.pulsecore.app.shop.application.assembler;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shop.api.dto.request.CreateOrderRequest;
import ru.pulsecore.app.shop.domain.*;
import ru.pulsecore.app.shop.infrastructure.config.ShopProperties;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class OrderAssembler {

    private final ShopProperties shopProperties;

    public OrderAssembler(ShopProperties shopProperties) {
        this.shopProperties = shopProperties;
    }

    public Order toOrder(UUID userId, CreateOrderRequest request, List<OrderItem> items) {
        BigDecimal total = items.stream()
                .map(i -> i.getProductPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String city = request.city();
        String street = request.street();

        if (request.deliveryMethod() == DeliveryMethod.PICKUP) {
            city = shopProperties.getPickup().getCity();
            street = shopProperties.getPickup().getAddress();
        }

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CONFIRMED)
                .paymentMethod(request.paymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryMethod(request.deliveryMethod())
                .customerFirstName(request.customerFirstName())
                .customerLastName(request.customerLastName())
                .customerMiddleName(request.customerMiddleName())
                .deliveryPhone(request.phone())
                .deliveryCity(city)
                .deliveryStreet(street)
                .comment(request.comment())
                .totalPrice(total)
                .build();

        items.forEach(i -> i.setOrder(order));
        order.setItems(items);
        return order;
    }
}