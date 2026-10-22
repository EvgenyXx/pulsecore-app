package ru.pulsecore.app.tournament.application.calculation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.pulsecore.app.tournament.application.calculation.league.place.PlacePointsCalculatorFactory;
import ru.pulsecore.app.tournament.domain.MatchCalculationStrategy;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.MatchNumber;
import ru.pulsecore.app.tournament.domain.enums.Place;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;
import ru.pulsecore.app.tournament.infrastructure.util.DateConstants;
import ru.pulsecore.app.shared.util.PlayerNameUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractFourPlayerRemovedStrategy implements MatchCalculationStrategy {

    protected final PlacePointsCalculatorFactory placePointsCalculatorFactory;

    /**
     * Возвращает места с учётом снятия. Каждая стратегия решает,
     * какое место присвоить снявшемуся.
     */
    protected abstract Map<String, Integer> determinePlacesWithWithdrawal(TournamentContext ctx);

    /**
     * Описание стратегии для логов (например, "removed-early").
     */
    protected abstract String logTag();

    @Override
    public MatchProcessingResult process(TournamentContext ctx) {
        PlacePointsCalculator calculator = placePointsCalculatorFactory.getCalculator(ctx.getLeague());
        LocalDate tournamentDate = parseDate(ctx.getDate());

        log.debug("🔍 [4pl_new / {}] id={}, лига={}, дата={}, матчей={}",
                logTag(), ctx.getTournamentId(), ctx.getLeague(),
                tournamentDate, ctx.getMatches().size());

        Map<String, Integer> placeMap = determinePlacesWithWithdrawal(ctx);
        Map<String, Integer> pointsMap = PointsResolver.calculatePoints(
                placeMap, calculator, ctx.getLeague(), tournamentDate);

        log.debug("📊 [4pl_new / {}] Итог: очки={}, места={}",
                logTag(), pointsMap, placeMap);

        return new MatchProcessingResult(pointsMap, placeMap);
    }

    // ===== ОБЩИЕ ХЕЛПЕРЫ =====

    protected void putFirstAndSecondPlaces(TournamentContext ctx, Map<String, Integer> places) {
        Match finalMatch = findMatch(ctx, MatchNumber.FINAL);
        if (finalMatch == null) return;

        places.put(norm(finalMatch.winnerOf()), Place.FIRST.getValue());
        places.put(norm(finalMatch.loserOf()), Place.SECOND.getValue());
    }

    protected Match findMatch(TournamentContext ctx, MatchNumber number) {
        for (Match m : ctx.getMatches()) {
            if (MatchNumber.fromNumber(m.getSortNumber()) == number) {
                return m;
            }
        }
        return null;
    }

    protected void addPlayer(List<String> names, String rawName) {
        if (rawName == null || rawName.isBlank()) return;
        names.add(norm(rawName));
    }

    protected String norm(String name) {
        return PlayerNameUtils.normalizeSearch(name);
    }

    protected LocalDate parseDate(String date) {
        if (date == null) return null;
        try {
            return LocalDate.parse(date, DateConstants.TOURNAMENT_DATE_FORMAT);
        } catch (Exception e) {
            log.warn("Не удалось распарсить дату турнира: {}", date);
            return null;
        }
    }
}