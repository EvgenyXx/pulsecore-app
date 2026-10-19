package ru.pulsecore.app.tournament.domain.enums;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum ComparePeriod {
    WEEK,
    MONTH,
    YEAR,
    ALL;

    public LocalDate startDate() {
        LocalDate today = LocalDate.now();
        return switch (this) {
            case WEEK -> today.with(DayOfWeek.MONDAY);
            case MONTH -> today.withDayOfMonth(1);
            case YEAR -> today.withDayOfYear(1);
            case ALL -> LocalDate.of(2000, 1, 1);
        };
    }

    public LocalDate endDate() {
        return LocalDate.now();
    }
}