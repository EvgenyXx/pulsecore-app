package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

public interface WeekStatsProjection {
    String getPlayerName();
    Double getTotal();
    Long getTournaments();
}