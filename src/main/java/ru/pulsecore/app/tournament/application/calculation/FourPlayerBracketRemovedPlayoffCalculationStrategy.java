package ru.pulsecore.app.tournament.application.calculation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.calculation.league.place.PlacePointsCalculatorFactory;
import ru.pulsecore.app.tournament.domain.MatchCalculationStrategy;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;
import ru.pulsecore.app.tournament.domain.enums.MatchNumber;
import ru.pulsecore.app.tournament.domain.enums.Place;
import ru.pulsecore.app.tournament.domain.enums.StrategyType;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;
import ru.pulsecore.app.tournament.infrastructure.util.DateConstants;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FourPlayerBracketRemovedPlayoffCalculationStrategy implements MatchCalculationStrategy {

    private final PlacePointsCalculatorFactory placePointsCalculatorFactory;

    @Override
    public StrategyType getType() {
        return StrategyType.FOUR_PL_BRACKET_REMOVED_PLAYOFF;
    }

    @Override
    public MatchProcessingResult process(TournamentContext ctx) {
        PlacePointsCalculator calculator = placePointsCalculatorFactory.getCalculator(ctx.getLeague());
        LocalDate tournamentDate = parseDate(ctx.getDate());

        log.debug("🔍 [4pl_new / removed-playoff] id={}, лига={}, дата={}, матчей={}",
                ctx.getTournamentId(), ctx.getLeague(), tournamentDate, ctx.getMatches().size());

        Map<String, Integer> placeMap = determinePlacesWithWithdrawal(ctx);
        Map<String, Integer> pointsMap = calculatePoints(placeMap, calculator, ctx.getLeague(), tournamentDate);

        log.debug("📊 [4pl_new / removed-playoff] Итог: очки={}, места={}", pointsMap, placeMap);
        return new MatchProcessingResult(pointsMap, placeMap);
    }

    // ===== ОПРЕДЕЛЕНИЕ МЕСТ =====

    private Map<String, Integer> determinePlacesWithWithdrawal(TournamentContext ctx) {
        Map<String, Integer> places = new HashMap<>();

        putWithdrawnPlace(ctx, places);
        putFirstAndSecondPlaces(ctx, places);
        putFourthPlace(ctx, places);

        return places;
    }

    /**
     * Снявшийся в 7-й → 3 место (полуфиналист, не сыграл финал).
     */
    private void putWithdrawnPlace(TournamentContext ctx, Map<String, Integer> places) {
        String withdrawn = findWithdrawnPlayer(ctx);
        if (withdrawn != null) {
            places.put(StringUtils.normalizeSearch(withdrawn), Place.THIRD.getValue());
        }
    }

    private void putFirstAndSecondPlaces(TournamentContext ctx, Map<String, Integer> places) {
        Match finalMatch = findMatch(ctx, MatchNumber.FINAL);
        if (finalMatch == null) return;

        places.put(StringUtils.normalizeSearch(finalMatch.winnerOf()), Place.FIRST.getValue());
        places.put(StringUtils.normalizeSearch(finalMatch.loserOf()), Place.SECOND.getValue());
    }

    /**
     * Проигравший 6-й игры → 4 место.
     */
    private void putFourthPlace(TournamentContext ctx, Map<String, Integer> places) {
        Match sixth = findMatch(ctx, MatchNumber.LOWER_MESH_3_GAME_1);
        if (sixth == null) return;

        places.put(StringUtils.normalizeSearch(sixth.loserOf()), Place.FOURTH.getValue());
    }

    // ===== ПОИСК СНЯВШЕГОСЯ =====

    /**
     * Сравниваем игроков 7-й игры с игроками 8-й.
     * Кто есть в 7-й, но нет в 8-й — снялся.
     */
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

    private void addPlayer(List<String> names, String rawName) {
        if (rawName == null || rawName.isBlank()) return;
        names.add(StringUtils.normalizeSearch(rawName));
    }

    // ===== ВСПОМОГАТЕЛЬНОЕ =====

    private Match findMatch(TournamentContext ctx, MatchNumber number) {
        for (Match m : ctx.getMatches()) {
            if (MatchNumber.fromNumber(m.getSortNumber()) == number) {
                return m;
            }
        }
        return null;
    }

    private Map<String, Integer> calculatePoints(Map<String, Integer> placeMap,
                                                 PlacePointsCalculator calculator,
                                                 LeagueType league,
                                                 LocalDate date) {
        Map<String, Integer> points = new LinkedHashMap<>();

        placeMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> {
                    int value = calculator.pointsForPlace(e.getValue(), league, date);
                    points.put(e.getKey(), value);
                    log.debug("🏁 {} — место: {}, очков: {}", e.getKey(), e.getValue(), value);
                });

        return points;
    }

    private LocalDate parseDate(String date) {
        if (date == null) return null;
        try {
            return LocalDate.parse(date, DateConstants.TOURNAMENT_DATE_FORMAT);
        } catch (Exception e) {
            log.warn("Не удалось распарсить дату турнира: {}", date);
            return null;
        }
    }
}