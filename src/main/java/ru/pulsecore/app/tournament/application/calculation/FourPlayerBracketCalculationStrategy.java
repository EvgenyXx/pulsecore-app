package ru.pulsecore.app.tournament.application.calculation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.calculation.league.place.PlacePointsCalculatorFactory;
import ru.pulsecore.app.tournament.domain.MatchCalculationStrategy;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.StrategyType;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;
import ru.pulsecore.app.tournament.infrastructure.util.DateConstants;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FourPlayerBracketCalculationStrategy implements MatchCalculationStrategy {

    private final PlacePointsCalculatorFactory placePointsCalculatorFactory;

    @Override
    public StrategyType getType() {
        return StrategyType.FOUR_PL_BRACKET;
    }

    @Override
    public MatchProcessingResult process(TournamentContext ctx) {
        PlacePointsCalculator calculator = placePointsCalculatorFactory.getCalculator(ctx.getLeague());
        LocalDate tournamentDate = parseDate(ctx.getDate());

        log.debug("🔍 [4pl_new] id={}, лига={}, дата={}, матчей={}",
                ctx.getTournamentId(), ctx.getLeague(), tournamentDate, ctx.getMatches().size());

        Map<String, Integer> placeMap = determinePlaces(ctx.getMatches());

        Map<String, Integer> pointsMap = new HashMap<>();
        for (Map.Entry<String, Integer> e : placeMap.entrySet()) {
            String player = e.getKey();
            int place = e.getValue();
            int points = calculator.pointsForPlace(place, ctx.getLeague(), tournamentDate);
            pointsMap.put(player, points);
            log.debug("🏁 {} — место: {}, очков: {}", player, place, points);
        }

        log.debug("📊 [4pl_new] Итог: очки={}, места={}", pointsMap, placeMap);
        return new MatchProcessingResult(pointsMap, placeMap);
    }

    /**
     * @return Map<нормализованное имя игрока, место>
     */
    private Map<String, Integer> determinePlaces(List<Match> matches) {
        Map<String, Integer> places = new HashMap<>();

        for (Match m : matches) {
            if ("final".equals(m.getGroupType())) {
                places.put(StringUtils.normalizeSearch(winnerOf(m)), 1);
                places.put(StringUtils.normalizeSearch(loserOf(m)), 2);
            } else if ("lower_mesh_3".equals(m.getGroupType())) {
                if (m.getSortNumber() == 7) places.put(StringUtils.normalizeSearch(loserOf(m)), 3);
                if (m.getSortNumber() == 6) places.put(StringUtils.normalizeSearch(loserOf(m)), 4);
            }
        }

        return places;
    }

    private String winnerOf(Match m) {
        if (m.getScore1() > m.getScore2()) {
            return m.getPlayer1();
        } else {
            return m.getPlayer2();
        }
    }

    private String loserOf(Match m) {
        if (m.getScore1() > m.getScore2()) {
            return m.getPlayer2();
        } else {
            return m.getPlayer1();
        }
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