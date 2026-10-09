package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateResponse(
        Long id,
        String name,
        String description,
        Long categoryId,
        String categoryName,
        String brand,
        BigDecimal price,
        String mainImageUrl,
        List<String> images,
        List<ProductColorDto> colors,
        List<ProductSizeDto> sizes,
        List<ProductVariantDto> variants
) {}