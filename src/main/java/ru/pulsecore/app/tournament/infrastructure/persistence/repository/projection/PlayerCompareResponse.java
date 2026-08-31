package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

import java.util.UUID;



public interface PlayerCompareResponse {
    UUID getPlayerId();
    String getPlayerName();
    String getPrimaryLeague();
    Long getTournaments();
    Double getTotalAmount();
    Double getAverageAmount();
}