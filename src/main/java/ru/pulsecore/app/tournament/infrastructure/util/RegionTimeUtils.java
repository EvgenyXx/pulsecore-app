package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import java.util.Map;

@UtilityClass
public class RegionTimeUtils {

    private static final LocalTime NIGHT_BORDER = LocalTime.of(6, 0);
    private static final ZoneId MSK = ZoneId.of("Europe/Moscow");

    private static final Map<Integer, ZoneId> HALL_TO_ZONE = Map.of(
            5, ZoneId.of("Asia/Vladivostok"),       // Уссурийск
            20, ZoneId.of("Asia/Yekaterinburg"),    // Оренбург
            21, ZoneId.of("Asia/Yekaterinburg")     // Оренбург
    );

    /**
     * Конвертирует МСК время в локальное для зала.
     */
    public LocalTime convertToRegion(LocalTime mskTime, int hall) {
        ZoneId zone = getZoneByHall(hall);
        return mskTime.atDate(LocalDate.now())
                .atZone(MSK)
                .withZoneSameInstant(zone)
                .toLocalTime();
    }

    /**
     * Возвращает таймзону по номеру зала.
     */
    public ZoneId getZoneByHall(int hall) {
        return HALL_TO_ZONE.getOrDefault(hall, MSK);
    }

    /**
     * Определяет — ночной ли турнир для региона.
     */
    public boolean isNight(LocalTime mskTime, int hall) {
        LocalTime regionTime = convertToRegion(mskTime, hall);
        return regionTime.isBefore(NIGHT_BORDER);
    }
}