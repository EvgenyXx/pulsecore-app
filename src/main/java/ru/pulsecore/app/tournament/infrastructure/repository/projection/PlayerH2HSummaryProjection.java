package ru.pulsecore.app.tournament.infrastructure.repository.projection;

public interface PlayerH2HSummaryProjection {
    Long getTotalMatches();
    Long getPlayer1Wins();
    Long getPlayer2Wins();
}