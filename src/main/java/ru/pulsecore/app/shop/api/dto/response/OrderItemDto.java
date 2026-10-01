package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long productId,
        String productName,
        String productBrand,
        String productImageUrl,
        BigDecimal productPrice,
        Integer quantity
) {}