package ru.pulsecore.app.shop.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductCreateResponse(
        Long id,
        String name,
        String description,
        Long categoryId,
        String categoryName,
        String brand,
        BigDecimal price,
        Integer stock,
        boolean active,
        String mainImageUrl,
        List<String> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}