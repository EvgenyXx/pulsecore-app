package ru.pulsecore.app.shop.api.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateRequest(

        String name,
        String description,
        String brand,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        String categoryName,

        List<ImageRequest> images
) {
    public record ImageRequest(
            String url,
            Integer sortOrder,
            boolean main
    ) {}
}