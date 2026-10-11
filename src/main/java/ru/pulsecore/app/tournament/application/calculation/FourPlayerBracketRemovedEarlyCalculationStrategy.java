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
public class FourPlayerBracketRemovedEarlyCalculationStrategy extends AbstractFourPlayerRemovedStrategy {

    public FourPlayerBracketRemovedEarlyCalculationStrategy(PlacePointsCalculatorFactory factory) {
        super(factory);
    }

    @Override
    public StrategyType getType() {
        return StrategyType.FOUR_PL_BRACKET_REMOVED_EARLY;
    }

    @Override
    protected String logTag() {
        return "removed-early";
    }

    @Override
    protected Map<String, Integer> determinePlacesWithWithdrawal(TournamentContext ctx) {
        Map<String, Integer> places = new HashMap<>();

        putWithdrawnPlace(ctx, places);
        putFirstAndSecondPlaces(ctx, places);
        putThirdPlace(ctx, places);

        return places;
    }

    /**
     * Снявшийся в 1–6 → 4 место.
     */
    private void putWithdrawnPlace(TournamentContext ctx, Map<String, Integer> places) {
        String withdrawn = findWithdrawnPlayer(ctx);
        if (withdrawn != null) {
            places.put(norm(withdrawn), Place.FOURTH.getValue());
        }
    }

    /**
     * Проигравший 7-й → 3 место.
     */
    private void putThirdPlace(TournamentContext ctx, Map<String, Integer> places) {
        Match semifinal = findMatch(ctx, MatchNumber.LOWER_MESH_3_GAME_2);
        if (semifinal == null) return;

        places.put(norm(semifinal.loserOf()), Place.THIRD.getValue());
    }

    // ===== ПОИСК СНЯВШЕГОСЯ =====

    private String findWithdrawnPlayer(TournamentContext ctx) {
        List<String> allNames = collectAllPlayers(ctx);
        List<String> playoffNames = collectPlayoffPlayers(ctx);

        for (String name : allNames) {
            if (!playoffNames.contains(name)) {
                return name;
            }
        }
        return null;
    }

    private List<String> collectAllPlayers(TournamentContext ctx) {
        List<String> names = new ArrayList<>();
        for (Match m : ctx.getMatches()) {
            MatchNumber num = MatchNumber.fromNumber(m.getSortNumber());
            if (num == MatchNumber.TOP_MESH_1_GAME_1 || num == MatchNumber.TOP_MESH_1_GAME_2) {
                addPlayer(names, m.getPlayer1());
                addPlayer(names, m.getPlayer2());
            }
        }
        return names;
    }

    private List<String> collectPlayoffPlayers(TournamentContext ctx) {
        List<String> names = new ArrayList<>();
        for (Match m : ctx.getMatches()) {
            MatchNumber num = MatchNumber.fromNumber(m.getSortNumber());
            if (num == MatchNumber.LOWER_MESH_3_GAME_2 || num == MatchNumber.FINAL) {
                addPlayer(names, m.getPlayer1());
                addPlayer(names, m.getPlayer2());
            }
        }
        return names;
    }
}