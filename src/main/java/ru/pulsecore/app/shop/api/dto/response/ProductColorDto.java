package ru.pulsecore.app.shop.api.dto.response;

import java.util.List;

public record ProductColorDto(
        Long id,
        String color,
        Integer sortOrder,
        List<ImageDto> images
) {
    public record ImageDto(
            Long id,
            String url,
            Integer sortOrder,
            boolean main
    ) {}
}