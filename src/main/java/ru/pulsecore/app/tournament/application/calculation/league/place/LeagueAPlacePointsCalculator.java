package ru.pulsecore.app.tournament.application.calculation.league.place;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

import java.time.LocalDate;


/**
 * Очки за место в лиге A (модель 4pl_new).
 * 1 — 9500, 2 — 7500, 3 — 5000, 4 — 3000.
 */
@Service
public class LeagueAPlacePointsCalculator implements PlacePointsCalculator {
    @Override
    public int pointsForPlace(int place, LeagueType league, LocalDate date) {
        return switch (place) {
            case 1 -> 9500;
            case 2 -> 7500;
            case 3 -> 5000;
            case 4 -> 3000;
            default -> 0;
        };
    }
}
