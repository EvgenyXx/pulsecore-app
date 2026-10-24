package ru.pulsecore.app.tournament.application.calculation.league.place;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

import java.time.LocalDate;

@Service
public class SuperLeaguePlacePointsCalculator implements PlacePointsCalculator {

    @Override
    public int pointsForPlace(int place, LeagueType league, LocalDate date) {
        return switch (place) {
            case 1 -> 11500;
            case 2 -> 9000;
            case 3 -> 6500;
            case 4 -> 4500;
            default -> 0;
        };
    }
}
