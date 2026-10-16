package ru.pulsecore.app.tournament.application.calculation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.calculation.league.place.PlacePointsCalculatorFactory;
import ru.pulsecore.app.tournament.domain.enums.MatchNumber;
import ru.pulsecore.app.tournament.domain.enums.Place;
import ru.pulsecore.app.tournament.domain.enums.StrategyType;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FourPlayerBracketRemovedPlayoffCalculationStrategy extends AbstractFourPlayerRemovedStrategy {

    public FourPlayerBracketRemovedPlayoffCalculationStrategy(PlacePointsCalculatorFactory factory) {
        super(factory);
    }

    @Override
    public StrategyType getType() {
        return StrategyType.FOUR_PL_BRACKET_REMOVED_PLAYOFF;
    }

    @Override
    protected String logTag() {
        return "removed-playoff";
    }

    @Override
    protected Map<String, Integer> determinePlacesWithWithdrawal(TournamentContext ctx) {
        Map<String, Integer> places = new HashMap<>();

        putWithdrawnPlace(ctx, places);
        putFirstAndSecondPlaces(ctx, places);
        putFourthPlace(ctx, places);

        return places;
    }

    /**
     * Снявшийся в 7-й → 3 место.
     */
    private void putWithdrawnPlace(TournamentContext ctx, Map<String, Integer> places) {
        String withdrawn = findWithdrawnPlayer(ctx);
        if (withdrawn != null) {
            places.put(norm(withdrawn), Place.THIRD.getValue());
        }
    }

    /**
     * Проигравший 6-й → 4 место.
     */
    private void putFourthPlace(TournamentContext ctx, Map<String, Integer> places) {
        Match sixth = findMatch(ctx, MatchNumber.LOWER_MESH_3_GAME_1);
        if (sixth == null) return;

        places.put(norm(sixth.loserOf()), Place.FOURTH.getValue());
    }

    // ===== ПОИСК СНЯВШЕГОСЯ =====

    private String findWithdrawnPlayer(TournamentContext ctx) {
        List<String> seventhNames = collectSeventhPlayers(ctx);
        List<String> eighthNames = collectEighthPlayers(ctx);

        for (String name : seventhNames) {
            if (!eighthNames.contains(name)) {
                return name;
            }
        }
        return null;
    }

    private List<String> collectSeventhPlayers(TournamentContext ctx) {
        List<String> names = new ArrayList<>();
        Match m = findMatch(ctx, MatchNumber.LOWER_MESH_3_GAME_2);
        if (m == null) return names;

        addPlayer(names, m.getPlayer1());
        addPlayer(names, m.getPlayer2());
        return names;
    }

    private List<String> collectEighthPlayers(TournamentContext ctx) {
        List<String> names = new ArrayList<>();
        Match m = findMatch(ctx, MatchNumber.FINAL);
        if (m == null) return names;

        addPlayer(names, m.getPlayer1());
        addPlayer(names, m.getPlayer2());
        return names;
    }
}