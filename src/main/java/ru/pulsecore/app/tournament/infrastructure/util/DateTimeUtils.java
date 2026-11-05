package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;
import ru.pulsecore.app.shared.dto.response.TournamentDto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Утилиты для парсинга и форматирования дат и времени турниров.
 */
@UtilityClass
public class DateTimeUtils {

    private static final DateTimeFormatter INPUT_FORMAT =
        new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .optionalStart()
                .appendPattern(" HH:mm:ss")
                .optionalStart() // <-- Добавляем еще один optional
                .appendPattern(".SSSSSS")
                .optionalEnd()   // <-- Закрываем
                .optionalEnd()
                .toFormatter();

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Форматирует период дат для отображения на фронте.
     *
     * @param start начало периода
     * @param end   конец периода
     * @return период в формате {@code dd.MM.yyyy - dd.MM.yyyy}
     *         или пустая строка, если одна из дат отсутствует
     */
    public static String formatPeriod(LocalDate start, LocalDate end) {
        if (start == null || end == null) return "";
        return start.format(DATE_FORMAT) + " - " + end.format(DATE_FORMAT);
    }

    /**
     * Форматирует дату из строки API.
     *
     * @param raw дата в формате {@code yyyy-MM-dd} или
     *            {@code yyyy-MM-dd HH:mm:ss.SSSSSS}
     * @return дата в формате {@code dd.MM.yyyy}
     */
    public static String formatDate(String raw) {
        return LocalDate.parse(raw, INPUT_FORMAT).format(DATE_FORMAT);
    }

    /**
     * Форматирует время из строки API.
     *
     * @param raw дата и время в формате {@code yyyy-MM-dd HH:mm:ss.SSSSSS}
     * @return время в формате {@code HH:mm} или {@code —}, если значение отсутствует
     */
    public static String formatTime(String raw) {
        if (raw == null) return "—";
        return LocalDateTime.parse(raw, INPUT_FORMAT).format(TIME_FORMAT);
    }

    /**
     * Извлекает дату из строки с датой или датой и временем.
     *
     * @param raw дата в формате {@code yyyy-MM-dd} или
     *            {@code yyyy-MM-dd HH:mm:ss.SSSSSS}
     * @return дата без времени или {@code null}, если значение отсутствует
     */
    public static LocalDate parseDate(String raw) {
        if (raw == null) return null;
        return LocalDate.parse(raw, INPUT_FORMAT);
    }

    /**
     * Извлекает время из строки API.
     *
     * @param raw дата и время в формате {@code yyyy-MM-dd HH:mm:ss.SSSSSS}
     * @return время в формате {@code HH:mm} или {@code null}, если значение отсутствует
     */
    public static String parseTime(String raw) {
        if (raw == null) return null;
        return LocalDateTime.parse(raw, INPUT_FORMAT).format(TIME_FORMAT);
    }

    /**
     * Проверяет, находится ли турнир в будущем.
     *
     * @param tournamentDto данные турнира
     * @return {@code true}, если дата и время турнира позже текущего момента
     */
    public static boolean isFuture(TournamentDto tournamentDto) {
        LocalDateTime dateTime = LocalDateTime.parse(
                tournamentDto.getDate().getDate(), INPUT_FORMAT);
        return dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Вычисляет количество минут до указанного времени турнира.
     *
     * @param time время турнира в формате {@code HH:mm}
     * @param now  текущее время
     * @return количество минут до турнира или {@code null}, если время турнира отсутствует
     */
    public static Long parseMinutesUntil(String time, LocalTime now) {
        if (time == null) return null;
        LocalTime tournamentTime = LocalTime.parse(time, TIME_FORMAT);
        return Duration.between(now, tournamentTime).toMinutes();
    }
}