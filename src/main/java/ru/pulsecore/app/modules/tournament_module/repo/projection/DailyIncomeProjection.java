package ru.pulsecore.app.modules.tournament_module.repo.projection;

public interface DailyIncomeProjection {
    Integer getDay();
    Double getTotal();
    Integer getCount();
}