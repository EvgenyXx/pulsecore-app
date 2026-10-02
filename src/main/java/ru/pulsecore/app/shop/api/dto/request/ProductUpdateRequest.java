package ru.pulsecore.app.shop.api.dto.request;

import java.math.BigDecimal;

public record ProductUpdateRequest(

        String name,
        String description,
        String brand,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        String categoryName
) {


}
