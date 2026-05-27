package ru.pulsecore.app.core.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.model.LeagueType;

@Component
@RequiredArgsConstructor
public class PointsCalculatorFactory {

    private final LeagueAPointsCalculator leagueA;
    private final LeagueBPointsCalculator leagueB;
    private final LeagueCPointsCalculator leagueC;
    private final LeagueDPointsCalculator leagueD;
    private final SuperLeagueCalculator superLeagueCalculator;



    public PointsCalculator getCalculator(LeagueType league) {

        return switch (league) {
            case A -> leagueA;
            case B -> leagueB;
            case C -> leagueC;
            case D -> leagueD;
            case SUPER_LEAGUE -> superLeagueCalculator;

        };
    }
}