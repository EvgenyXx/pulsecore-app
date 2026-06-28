package ru.pulsecore.app.modules.tournament.api.dto.projection;

public interface LeagueStatProjection {
    String getLeague();
    Long getCount();
    Double getSum();
    Double getAvg();
}
