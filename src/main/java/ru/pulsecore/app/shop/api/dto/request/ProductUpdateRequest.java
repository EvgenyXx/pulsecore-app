package ru.pulsecore.app.shop.api.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateRequest(
        String name,
        String description,
        String brand,
        BigDecimal price,
        Long categoryId,
        List<ProductColorRequest> colors,
        List<ProductSizeRequest> sizes,
        List<ProductVariantRequest> variants
) {}