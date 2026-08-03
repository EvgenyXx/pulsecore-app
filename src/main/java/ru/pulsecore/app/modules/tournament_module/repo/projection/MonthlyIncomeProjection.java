package ru.pulsecore.app.modules.tournament_module.repo.projection;

public interface MonthlyIncomeProjection {
    String getMonth();
    Double getTotal();
    Long getCount();
    Double getAverage();
}