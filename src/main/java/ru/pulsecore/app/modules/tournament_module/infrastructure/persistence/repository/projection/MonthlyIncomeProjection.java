package ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.repository.projection;

public interface MonthlyIncomeProjection {
    String getMonth();
    Double getTotal();
    Long getCount();
    Double getAverage();
}