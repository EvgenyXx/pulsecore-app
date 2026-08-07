package ru.pulsecore.app.shared.dto.response;

import java.util.UUID;

public record PlayerData(
        UUID playerId,
        String playerName,
        String email,
        String primaryLeague,
        boolean pushEnabled,
        boolean notificationsEnabled
) {}