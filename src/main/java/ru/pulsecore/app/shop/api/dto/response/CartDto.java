package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        Long id,
        List<CartItemDto> items,
        Integer totalQuantity,
        BigDecimal totalPrice
) {}