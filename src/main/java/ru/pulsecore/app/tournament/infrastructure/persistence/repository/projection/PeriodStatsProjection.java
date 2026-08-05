package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

public interface PeriodStatsProjection {
    Double getSum();
    Double getAverage();
    Double getMinusThreePercent();
    Long getCount();
}