package ru.pulsecore.app.shared.event;

import java.util.UUID;

public record PlayerCreatedEvent(
        UUID playerId,
        String playerName,
        String email,
        int days,
        String ip,
        String userAgent
) {

}