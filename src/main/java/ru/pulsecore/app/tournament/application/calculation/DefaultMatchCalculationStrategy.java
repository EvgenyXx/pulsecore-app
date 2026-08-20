package ru.pulsecore.app.tournament.application.calculation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.calculation.league.PointsCalculatorFactory;
import ru.pulsecore.app.tournament.infrastructure.util.DateConstants;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.application.calculation.league.PlacementCalculator;
import ru.pulsecore.app.tournament.domain.PointsCalculator;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;
import ru.pulsecore.app.tournament.domain.MatchCalculationStrategy;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.enums.StrategyType;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultMatchCalculationStrategy implements MatchCalculationStrategy {

    private final PlacementCalculator placementCalculator;
    private final PointsCalculatorFactory factory;

    @Override
    public StrategyType getType() {
        return StrategyType.DEFAULT;
    }

    @Override
    public MatchProcessingResult process(TournamentContext ctx) {
        PointsCalculator calculator = factory.getCalculator(ctx.getLeague());
        LocalDate tournamentDate = parseDate(ctx.getDate());

        log.debug("🔍 Начало расчёта. Турнир: id={}, лига={}, дата={}, матчей={}",
                ctx.getTournamentId(), ctx.getLeague(), tournamentDate, ctx.getMatches().size());

        Map<String, Integer> pointsMap = new HashMap<>();
        Map<String, Integer> placeMap = new HashMap<>();

        for (Match m : ctx.getMatches()) {
            if (isCompletedMatch(m)) {
                processMatch(m, calculator, pointsMap, placeMap, tournamentDate);
            } else {
                log.debug("⏭ Матч пропущен: {} vs {} — статус: {}, счёт: {}:{}",
                        m.getPlayer1(), m.getPlayer2(), m.getStatus(), m.getScore1(), m.getScore2());
            }
        }

        log.debug("📊 Итог расчёта: очки={}, места={}", pointsMap, placeMap);
        return new MatchProcessingResult(pointsMap, placeMap);
    }

    private void processMatch(Match m, PointsCalculator calculator,
                              Map<String, Integer> pointsMap, Map<String, Integer> placeMap,
                              LocalDate tournamentDate) {
        String p1 = StringUtils.normalizeSearch(m.getPlayer1());
        String p2 = StringUtils.normalizeSearch(m.getPlayer2());

        log.debug("⚔️ Матч: {} vs {} | счёт: {}:{} | статус: {} | этап: {}",
                p1, p2, m.getScore1(), m.getScore2(), m.getStatus(), m.getStage());

        int p1Points = calculator.calculatePoints(m, tournamentDate);
        log.debug("🎯 {}({}) — очков: {}", p1, m.getPlayer1(), p1Points);
        pointsMap.merge(p1, p1Points, Integer::sum);

        int p1Place = placementCalculator.calculatePlace(m);
        log.debug("🏁 {}({}) — место: {}", p1, m.getPlayer1(), p1Place);
        if (p1Place != 0) {
            placeMap.put(p1, p1Place);
        }

        Match reversed = m.reverse();
        int p2Points = calculator.calculatePoints(reversed, tournamentDate);
        log.debug("🎯 {}({}) — очков: {}", p2, m.getPlayer2(), p2Points);
        pointsMap.merge(p2, p2Points, Integer::sum);

        int p2Place = placementCalculator.calculatePlace(reversed);
        log.debug("🏁 {}({}) — место: {}", p2, m.getPlayer2(), p2Place);
        if (p2Place != 0) {
            placeMap.put(p2, p2Place);
        }
    }

    private boolean isCompletedMatch(Match m) {
        boolean completed = m.getStatus() != null
                && m.getStatus().toLowerCase().contains("заверш")
                && (m.getScore1() + m.getScore2() > 0);

        if (!completed) {
            log.debug("⏭ Матч не завершён: {} vs {} — статус: {}, счёт: {}:{}",
                    m.getPlayer1(), m.getPlayer2(), m.getStatus(), m.getScore1(), m.getScore2());
        }
        return completed;
    }

    private LocalDate parseDate(String date) {
        if (date == null) {
            log.debug("⚠️ Дата турнира null");
            return null;
        }
        try {
            return LocalDate.parse(date, DateConstants.TOURNAMENT_DATE_FORMAT);
        } catch (Exception e) {
            log.warn("Не удалось распарсить дату турнира: {}", date);
            return null;
        }
    }
}