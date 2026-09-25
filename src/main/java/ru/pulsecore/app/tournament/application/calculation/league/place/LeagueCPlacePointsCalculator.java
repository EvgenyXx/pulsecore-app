package ru.pulsecore.app.tournament.application.calculation.league.place;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

import java.time.LocalDate;

/**
 * Очки за место в лиге C (модель 4pl_new).
 * 1 — 5500, 2 — 4500, 3 — 3000, 4 — 1500.
 */
@Service
public class LeagueCPlacePointsCalculator implements PlacePointsCalculator {

    @Override
    public int pointsForPlace(int place, LeagueType league, LocalDate date) {
        return switch (place) {
            case 1 -> 5500;
            case 2 -> 4500;
            case 3 -> 3000;
            case 4 -> 1500;
            default -> 0;
        };
    }
}