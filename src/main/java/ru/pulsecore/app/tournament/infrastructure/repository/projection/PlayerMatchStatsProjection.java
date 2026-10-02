package ru.pulsecore.app.tournament.infrastructure.repository.projection;

public interface PlayerMatchStatsProjection {
    String getPlayerName();
    Double getGroupWinPercent();
    Double getSemifinalWinPercent();
    Double getThirdPlaceWinPercent();
    Double getFinalWinPercent();
}