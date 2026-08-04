package ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.repository.projection;

public interface DailyIncomeProjection {
    Integer getDay();
    Double getTotal();
    Integer getCount();
}