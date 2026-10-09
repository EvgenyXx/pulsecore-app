package ru.pulsecore.app.shop.api.dto.request;

public record ProductSizeRequest(
        String size,
        Integer sortOrder
) {}