package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailDto(
        Long id,
        String name,
        String description,
        String brand,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        String categoryName,
        List<ImageDto> images
) {
    public record ImageDto(Long id, String url) {}
}