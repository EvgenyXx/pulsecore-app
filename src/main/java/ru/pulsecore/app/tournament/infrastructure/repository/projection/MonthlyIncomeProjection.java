package ru.pulsecore.app.tournament.infrastructure.repository.projection;

public interface MonthlyIncomeProjection {
    String getMonth();
    Double getTotal();
    Long getCount();
    Double getAverage();
}