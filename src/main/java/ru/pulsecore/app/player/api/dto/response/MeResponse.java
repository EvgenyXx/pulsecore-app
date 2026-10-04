package ru.pulsecore.app.player.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record MeResponse(
        String id,
        String name,
        String email,
        LocalDateTime createdAt,
        List<String> roles,
        String theme) {
}