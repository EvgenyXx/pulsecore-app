package ru.pulsecore.app.shared.dto.response;

import java.time.LocalDateTime;

public record LastLoginResponse(
        String name,
        LocalDateTime lastLoginAt
) {
}
