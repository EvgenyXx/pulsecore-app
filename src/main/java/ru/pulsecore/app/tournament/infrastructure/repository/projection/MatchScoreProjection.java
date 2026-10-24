package ru.pulsecore.app.tournament.infrastructure.repository.projection;

public interface MatchScoreProjection {
    String getScore();
    String getWinnerName();
}