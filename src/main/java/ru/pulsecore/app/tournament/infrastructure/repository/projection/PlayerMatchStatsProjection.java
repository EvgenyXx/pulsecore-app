package ru.pulsecore.app.tournament.infrastructure.repository.projection;

public interface PlayerMatchStatsProjection {
    String getPlayerName();
    Long getTotalMatches();
    Long getWins();
}