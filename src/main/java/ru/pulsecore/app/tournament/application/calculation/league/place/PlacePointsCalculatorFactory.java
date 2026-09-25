package ru.pulsecore.app.tournament.application.calculation.league.place;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

@Component
@RequiredArgsConstructor
public class PlacePointsCalculatorFactory {

    private final LeagueBPlacePointsCalculator leagueB;
    //    private final LeagueAPlacePointsCalculator leagueA;
    private final LeagueCPlacePointsCalculator leagueC;
//    private final LeagueDPlacePointsCalculator leagueD;
//    private final SuperLeaguePlacePointsCalculator superLeague;

    public PlacePointsCalculator getCalculator(LeagueType league) {
        return switch (league) {
            case B -> leagueB;
//            case A -> leagueA;
            case C -> leagueC;
//            case D -> leagueD;
//            case SUPER_LEAGUE -> superLeague;
            default -> throw new IllegalStateException(
                    "PlacePointsCalculator не реализован для лиги: " + league);
        };
    }
}