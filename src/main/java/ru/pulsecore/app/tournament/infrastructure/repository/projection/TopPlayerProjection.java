package ru.pulsecore.app.tournament.infrastructure.repository.projection;

import java.util.UUID;

public interface TopPlayerProjection {
    UUID getPlayerId();
    String getName();
    String getPrimaryLeague();
    Double getTotal();
    Long getTournaments();
}