package ru.pulsecore.app.tournament.application.calculation.league.place;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

import java.time.LocalDate;

/**
 * Очки за место в лиге B (модель 4pl_new).
 * 1 — 7000, 2 — 5500, 3 — 4500, 4 — 2000.
 */
@Service
public class LeagueBPlacePointsCalculator implements PlacePointsCalculator {

    @Override
    public int pointsForPlace(int place, LeagueType league, LocalDate date) {
        return switch (place) {
            case 1 -> 7000;
            case 2 -> 5500;
            case 3 -> 4500;
            case 4 -> 2000;
            default -> 0;
        };
    }
}