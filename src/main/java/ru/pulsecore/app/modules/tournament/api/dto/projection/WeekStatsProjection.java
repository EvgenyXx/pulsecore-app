package ru.pulsecore.app.modules.tournament.api.dto.projection;

public interface WeekStatsProjection {
    String getPlayerName();
    Double getTotal();
    Long getTournaments();
}