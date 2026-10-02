package ru.pulsecore.app.tournament.infrastructure.repository.projection;

public interface LeagueStatProjection {
    String getLeague();
    Long getCount();
    Double getSum();
    Double getAvg();
}
