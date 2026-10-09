package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long productId,
        Long variantId,
        String productName,
        String productBrand,
        String variantSize,
        String variantColor,
        String productImageUrl,
        BigDecimal productPrice,
        Integer quantity
) {}