package ru.pulsecore.app.tournament.infrastructure.repository.projection;

import java.util.UUID;

public interface PrimaryLeagueProjection {
    UUID getPlayerId();
    String getLeague();
}