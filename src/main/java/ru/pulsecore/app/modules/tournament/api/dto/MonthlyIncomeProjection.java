package ru.pulsecore.app.modules.tournament.api.dto;

public interface MonthlyIncomeProjection {
    String getMonth();
    Double getTotal();
    Long getCount();
    Double getAverage();
}