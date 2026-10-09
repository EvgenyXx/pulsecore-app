package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;

public record CartItemDto(
        Long id,
        Long variantId,
        Long productId,
        String name,
        String brand,
        String image,
        BigDecimal price,
        Integer quantity,
        Integer stock,
        String size,
        String color
) {}