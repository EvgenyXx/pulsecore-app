package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailDto(
        Long id,
        String name,
        String description,
        String brand,
        BigDecimal price,
        Long categoryId,
        String categoryName,
        List<ProductColorDto> colors,
        List<ProductSizeDto> sizes,
        List<ProductVariantDto> variants,
        boolean inStock
) {}