package ru.pulsecore.app.modules.tournament_module.repo.projection;

public interface LeagueStatProjection {
    String getLeague();
    Long getCount();
    Double getSum();
    Double getAvg();
}
