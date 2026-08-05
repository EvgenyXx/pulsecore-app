package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

public interface MonthlyIncomeProjection {
    String getMonth();
    Double getTotal();
    Long getCount();
    Double getAverage();
}