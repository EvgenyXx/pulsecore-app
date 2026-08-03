package ru.pulsecore.app.modules.tournament_module.repo.projection;

public interface WeekStatsProjection {
    String getPlayerName();
    Double getTotal();
    Long getTournaments();
}