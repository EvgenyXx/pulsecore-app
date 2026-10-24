package ru.pulsecore.app.tournament.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public enum ComparePeriod {
    WEEK("week"),
    MONTH("month"),
    YEAR("year"),
    ALL("all");

    private final String value;

    public LocalDate startDate() {
        LocalDate today = LocalDate.now();
        return switch (this) {
            case WEEK -> today.with(DayOfWeek.MONDAY);
            case MONTH -> today.withDayOfMonth(1);
            case YEAR -> today.withDayOfYear(1);
            case ALL -> LocalDate.of(2000, 1, 1);
        };
    }

    public static ComparePeriod fromCode(String value){
        for (ComparePeriod comparePeriod : values()){
            if (value.equalsIgnoreCase(comparePeriod.value)){
                return comparePeriod;
            }
        }
        return ComparePeriod.ALL;
    }

    public LocalDate endDate() {
        return LocalDate.now();
    }
}