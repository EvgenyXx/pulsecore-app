package ru.pulsecore.app.tournament.application.calculation;

import lombok.experimental.UtilityClass;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class PointsResolver {

    /**
     * Начисляет очки по местам. Возвращает LinkedHashMap, отсортированный по месту.
     */
    public Map<String, Integer> calculatePoints(Map<String, Integer> placeMap,
                                                PlacePointsCalculator calculator,
                                                LeagueType league,
                                                LocalDate date) {
        Map<String, Integer> points = new LinkedHashMap<>();

        placeMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> {
                    int value = calculator.pointsForPlace(e.getValue(), league, date);
                    points.put(e.getKey(), value);
                });

        return points;
    }
}