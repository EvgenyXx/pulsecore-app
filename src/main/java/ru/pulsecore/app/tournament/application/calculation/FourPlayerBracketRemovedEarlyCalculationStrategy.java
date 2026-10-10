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
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FourPlayerBracketRemovedEarlyCalculationStrategy implements MatchCalculationStrategy {

    private final PlacePointsCalculatorFactory placePointsCalculatorFactory;

    @Override
    public StrategyType getType() {
        return StrategyType.FOUR_PL_BRACKET_REMOVED_EARLY;
    }

    @Override
    public MatchProcessingResult process(TournamentContext ctx) {
        PlacePointsCalculator calculator = placePointsCalculatorFactory.getCalculator(ctx.getLeague());
        LocalDate tournamentDate = parseDate(ctx.getDate());

        log.debug("🔍 [4pl_new / removed-early] id={}, лига={}, дата={}, матчей={}",
                ctx.getTournamentId(), ctx.getLeague(), tournamentDate, ctx.getMatches().size());

        Map<String, Integer> placeMap = determinePlacesWithWithdrawal(ctx);

        Map<String, Integer> pointsMap = calculatePoints(placeMap, calculator, ctx.getLeague(), tournamentDate);

        log.debug("📊 [4pl_new / removed-early] Итог: очки={}, места={}", pointsMap, placeMap);
        return new MatchProcessingResult(pointsMap, placeMap);
    }

    // ===== ОПРЕДЕЛЕНИЕ МЕСТ =====

    private Map<String, Integer> determinePlacesWithWithdrawal(TournamentContext ctx) {
        Map<String, Integer> places = new HashMap<>();

        putWithdrawnPlace(ctx, places);
        putFirstAndSecondPlaces(ctx, places);
        putThirdPlace(ctx, places);

        return places;
    }

    private void putWithdrawnPlace(TournamentContext ctx, Map<String, Integer> places) {
        String withdrawn = findWithdrawnPlayer(ctx);
        if (withdrawn != null) {
            places.put(StringUtils.normalizeSearch(withdrawn), Place.FOURTH.getValue());
        }
    }

    private void putFirstAndSecondPlaces(TournamentContext ctx, Map<String, Integer> places) {
        Match finalMatch = findMatch(ctx, MatchNumber.FINAL);
        if (finalMatch == null) return;

        places.put(StringUtils.normalizeSearch(finalMatch.winnerOf()), Place.FIRST.getValue());
        places.put(StringUtils.normalizeSearch(finalMatch.loserOf()), Place.SECOND.getValue());
    }

    private void putThirdPlace(TournamentContext ctx, Map<String, Integer> places) {
        Match semifinal = findMatch(ctx, MatchNumber.LOWER_MESH_3_GAME_2);
        if (semifinal == null) return;

        places.put(StringUtils.normalizeSearch(semifinal.loserOf()), Place.THIRD.getValue());
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

    /**
     * Все игроки турнира — из первых двух матчей (top_mesh_1 game 1 и game 2).
     */
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

    /**
     * Игроки 7 и 8 матчей (полуфинал + финал).
     */
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