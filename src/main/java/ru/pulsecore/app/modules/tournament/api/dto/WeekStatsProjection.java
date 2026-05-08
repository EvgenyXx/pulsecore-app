package ru.pulsecore.app.modules.tournament.api.dto;

public interface WeekStatsProjection {
    String getPlayerName();
    Double getTotal();
    Long getTournaments();
}