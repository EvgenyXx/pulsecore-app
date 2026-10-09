package ru.pulsecore.app.shop.api.dto.request;

import java.math.BigDecimal;

public record ProductVariantRequest(
        String color,
        String size,
        Integer stock,
        BigDecimal priceDelta
) {}