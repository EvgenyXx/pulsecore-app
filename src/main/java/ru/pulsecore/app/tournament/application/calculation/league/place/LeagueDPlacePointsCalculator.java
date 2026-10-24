package ru.pulsecore.app.tournament.application.calculation.league.place;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

import java.time.LocalDate;


@Service
public class LeagueDPlacePointsCalculator implements PlacePointsCalculator {


    @Override
    public int pointsForPlace(int place, LeagueType league, LocalDate date) {
        return switch (place) {
            case 1 -> 4500;
            case 2 -> 3000;
            case 3 -> 2500;
            case 4 -> 1000;
            default -> 0;
        };
    }
}
