package ru.pulsecore.app.tournament.infrastructure.repository.projection;

import java.util.UUID;

public interface PlayerTopStatsProjection {
    UUID getPlayerId();
    Long getTournaments();
    Double getTotalAmount();
    Double getAverageAmount();
}