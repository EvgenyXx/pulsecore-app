package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

@UtilityClass
public class MonthUtils {

    private static final DateTimeFormatter MONTH_FORMATTER = new DateTimeFormatterBuilder()
            .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL)
            .toFormatter(new Locale("ru"));

    public String toRussianMonthYear(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("LLLL yyyy", new Locale("ru")));
    }
}