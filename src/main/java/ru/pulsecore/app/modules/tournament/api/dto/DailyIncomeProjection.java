package ru.pulsecore.app.modules.tournament.api.dto;

public interface DailyIncomeProjection {
    Integer getDay();
    Double getTotal();
    Integer getCount();
}