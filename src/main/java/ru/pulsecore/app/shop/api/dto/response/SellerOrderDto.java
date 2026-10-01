package ru.pulsecore.app.shop.api.dto.response;

import ru.pulsecore.app.shop.domain.DeliveryMethod;
import ru.pulsecore.app.shop.domain.OrderStatus;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SellerOrderDto(
        Long id,
        OrderStatus status,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        DeliveryMethod deliveryMethod,

        String customerFirstName,
        String customerLastName,
        String customerMiddleName,
        String deliveryPhone,
        String deliveryCity,
        String deliveryStreet,
        String comment,

        BigDecimal totalPrice,
        List<OrderItemDto> items,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}