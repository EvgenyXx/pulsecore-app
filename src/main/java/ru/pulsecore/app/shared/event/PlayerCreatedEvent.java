package ru.pulsecore.app.shared.event;

import java.util.UUID;

public record PlayerCreatedEvent(
        UUID playerId,
        String playerName,
        String email,
        String ip,
        String userAgent,
        boolean silent
) {

}