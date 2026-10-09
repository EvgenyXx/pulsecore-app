package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;

public record ProductVariantDto(
        Long id,
        Long colorId,
        String color,
        Long sizeId,
        String size,
        Integer stock,
        BigDecimal priceDelta
) {}