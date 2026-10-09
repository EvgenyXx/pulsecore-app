package ru.pulsecore.app.shop.api.dto.response;

public record ProductSizeDto(
        Long id,
        String size,
        Integer sortOrder
) {}