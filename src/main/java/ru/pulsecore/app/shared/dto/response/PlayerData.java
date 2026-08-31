package ru.pulsecore.app.shared.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PlayerData(
        UUID playerId,
        String playerName,
        String email,
        String primaryLeague,
        boolean pushEnabled,
        boolean notificationsEnabled,
        boolean hasActiveSubscription,
        String selectedHalls,
        String liveSelectedHalls,
        LocalDateTime lastLoginAt
) {}