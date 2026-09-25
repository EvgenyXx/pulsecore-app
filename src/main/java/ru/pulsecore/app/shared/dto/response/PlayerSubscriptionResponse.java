package ru.pulsecore.app.shared.dto.response;

import java.time.LocalDateTime;

public record PlayerSubscriptionResponse(
        String name,
        Boolean active,
        LocalDateTime expiresAt
) {}