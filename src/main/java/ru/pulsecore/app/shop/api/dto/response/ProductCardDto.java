package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductCardDto(
        Long id,
        String name,
        String brand,
        BigDecimal price,
        String mainImageUrl,
        List<String> images,
        Long categoryId,
        String categoryName,
        Integer stock,
        boolean inStock

) {
}