package ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.repository.projection;

public interface WeekStatsProjection {
    String getPlayerName();
    Double getTotal();
    Long getTournaments();
}