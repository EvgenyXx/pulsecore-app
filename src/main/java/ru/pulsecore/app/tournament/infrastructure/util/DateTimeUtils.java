package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static String formatDate(String raw) {
        if (raw == null) return "—";
        try {
            LocalDateTime dt = LocalDateTime.parse(raw, INPUT_FORMAT);
            return dt.format(DATE_FORMAT);
        } catch (Exception e) {
            return raw.split(" ")[0];
        }
    }

    public static String formatTime(String raw) {
        if (raw == null) return "—";
        try {
            LocalDateTime dt = LocalDateTime.parse(raw, INPUT_FORMAT);
            return dt.format(TIME_FORMAT);
        } catch (Exception e) {
            try {
                return raw.split(" ")[1].substring(0, 5);
            } catch (Exception ex) {
                return "—";
            }
        }
    }


    /**
     * Извлекает LocalDate из строки API.
     * Формат входа: "yyyy-MM-dd HH:mm:ss.SSSSSS"
     */
    public static LocalDate parseDate(String raw) {
        if (raw == null) return null;
        try {
            return LocalDateTime.parse(raw, INPUT_FORMAT).toLocalDate();
        } catch (Exception e) {
            return LocalDate.parse(raw.split(" ")[0]);
        }
    }

    /**
     * Извлекает время "HH:mm" из строки API.
     * Формат входа: "yyyy-MM-dd HH:mm:ss.SSSSSS"
     */
    public static String parseTime(String raw) {
        if (raw == null) return null;
        try {
            return LocalDateTime.parse(raw, INPUT_FORMAT).format(TIME_FORMAT);
        } catch (Exception e) {
            return raw.split(" ")[1].substring(0, 5);
        }
    }

    public static boolean isFuture(String raw) {
        if (raw == null) return false;
        try {
            LocalDateTime dt = LocalDateTime.parse(raw, INPUT_FORMAT);
            return dt.isAfter(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }

    public static Long parseMinutesUntil(String time, LocalTime now) {
        if (time == null) return null;
        try {
            LocalTime tournamentTime = LocalTime.parse(time, TIME_FORMAT);
            return java.time.Duration.between(now, tournamentTime).toMinutes();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean shouldSendHourReminder(String time, LocalTime now) {
        if (time == null || time.isEmpty()) return false;
        Long minutes = parseMinutesUntil(time, now);
        return minutes != null && minutes > 0 && minutes <= 60;
    }
}