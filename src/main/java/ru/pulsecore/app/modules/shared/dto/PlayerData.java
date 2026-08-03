package ru.pulsecore.app.modules.shared.dto;

import java.util.UUID;

public record PlayerData(
        UUID playerId,
        String playerName,
        String email) {
}
