package ru.pulsecore.app.shared.dto.response;



import java.time.LocalDateTime;


public record SubscriptionStatusResponse(
        boolean activeNow,
        LocalDateTime expiresAt,
        LocalDateTime startedAt) {
}