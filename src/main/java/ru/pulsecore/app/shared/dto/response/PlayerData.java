package ru.pulsecore.app.shared.dto.response;

import ru.pulsecore.app.player.domain.enums.City;

import java.time.LocalDateTime;
import java.util.UUID;

public record PlayerData(
        UUID id,
        String name,
        String email,
        String primaryLeague,
        boolean pushEnabled,
        boolean notificationsEnabled,
        boolean hasActiveSubscription,
        String selectedHalls,
        String liveSelectedHalls,
        LocalDateTime lastLoginAt,
        City city
) {}