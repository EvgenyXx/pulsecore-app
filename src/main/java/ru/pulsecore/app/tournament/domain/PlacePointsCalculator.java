package ru.pulsecore.app.tournament.domain;

import ru.pulsecore.app.tournament.domain.enums.LeagueType;

import java.time.LocalDate;

/**
 * Калькулятор очков за МЕСТО в турнире (новая модель для 4pl_new).
 * Не путать с {@link PointsCalculator} — тот считает очки за матч по сетам.
 */
public interface PlacePointsCalculator {

    /**
     * @param place   место игрока (1..4)
     * @param league  лига турнира
     * @param date    дата турнира (может быть null)
     * @return очки за это место
     */
    int pointsForPlace(int place, LeagueType league, LocalDate date);
}