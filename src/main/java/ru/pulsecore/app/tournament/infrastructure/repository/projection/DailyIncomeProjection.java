package ru.pulsecore.app.tournament.infrastructure.repository.projection;

public interface DailyIncomeProjection {
    Integer getDay();
    Double getTotal();
    Integer getCount();
}