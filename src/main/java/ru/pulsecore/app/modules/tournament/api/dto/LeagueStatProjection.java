package ru.pulsecore.app.modules.tournament.api.dto;

public interface LeagueStatProjection {
    String getLeague();
    Long getCount();
    Double getSum();
    Double getAvg();
}
