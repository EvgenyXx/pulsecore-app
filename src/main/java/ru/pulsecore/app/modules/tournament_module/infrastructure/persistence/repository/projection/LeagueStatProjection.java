package ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.repository.projection;

public interface LeagueStatProjection {
    String getLeague();
    Long getCount();
    Double getSum();
    Double getAvg();
}
