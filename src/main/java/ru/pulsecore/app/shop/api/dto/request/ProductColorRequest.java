package ru.pulsecore.app.shop.api.dto.request;

import java.util.List;

public record ProductColorRequest(
        String color,
        Integer sortOrder,
        List<ImageRequest> images
) {
    public record ImageRequest(
            String url,
            Integer sortOrder,
            boolean main
    ) {}
}