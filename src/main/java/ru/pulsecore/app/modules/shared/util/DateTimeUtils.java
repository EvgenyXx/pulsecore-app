package ru.pulsecore.app.modules.shared.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
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
}