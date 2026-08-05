package ru.pulsecore.app.modules.tournament.infrastructure.persistence.repository.projection;

public interface DailyIncomeProjection {
    Integer getDay();
    Double getTotal();
    Integer getCount();
}