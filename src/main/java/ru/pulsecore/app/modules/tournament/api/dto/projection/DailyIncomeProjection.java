package ru.pulsecore.app.modules.tournament.api.dto.projection;

public interface DailyIncomeProjection {
    Integer getDay();
    Double getTotal();
    Integer getCount();
}