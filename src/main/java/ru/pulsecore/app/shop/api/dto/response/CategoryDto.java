package ru.pulsecore.app.shop.api.dto.response;

import java.time.LocalDateTime;

public record CategoryDto(
        Long id,
        String name,
        boolean active,
        LocalDateTime createdAt
) {}