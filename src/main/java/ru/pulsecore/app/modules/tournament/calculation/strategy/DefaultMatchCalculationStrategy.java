package ru.pulsecore.app.modules.tournament.calculation.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.DateConstants;
import ru.pulsecore.app.core.model.Match;
import ru.pulsecore.app.core.stats.PlacementCalculator;
import ru.pulsecore.app.core.stats.PointsCalculator;
import ru.pulsecore.app.core.stats.PointsCalculatorFactory;
import ru.pulsecore.app.modules.tournament.domain.MatchProcessingResult;
import ru.pulsecore.app.modules.tournament.domain.TournamentContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultMatchCalculationStrategy implements MatchCalculationStrategy {

    private final PlacementCalculator placementCalculator;
    private final PointsCalculatorFactory factory;



    //todo добавить умножение турниров 31 и 1 числа в новый год
    @Override
    public StrategyType getType() {
        return StrategyType.DEFAULT;
    }

    @Override
    public MatchProcessingResult process(TournamentContext ctx) {

        PointsCalculator calculator = factory.getCalculator(ctx.getLeague());
        LocalDate tournamentDate = parseDate(ctx.getDate());

        Map<String, Integer> pointsMap = new HashMap<>();
        Map<String, Integer> placeMap = new HashMap<>();

        if (log.isDebugEnabled()) {
            log.debug("▶ Default strategy started: tournamentId={}, matches={}",
                    ctx.getTournamentId(),
                    ctx.getMatches().size());
        }

        for (Match m : ctx.getMatches()) {
            if (!isCompletedMatch(m)) {
                continue;
            }
            processMatch(m, calculator, pointsMap, placeMap, tournamentDate);
        }

        if (log.isDebugEnabled()) {
            log.debug("✔ Default strategy finished: players={}, pointsCalculated={}",
                    pointsMap.size(),
                    pointsMap.values().stream().mapToInt(Integer::intValue).sum());
        }

        return new MatchProcessingResult(pointsMap, placeMap);
    }

    private void processMatch(Match m,
                              PointsCalculator calculator,
                              Map<String, Integer> pointsMap,
                              Map<String, Integer> placeMap,
                              LocalDate tournamentDate) {

        String p1 = normalize(m.getPlayer1());
        String p2 = normalize(m.getPlayer2());

        int p1Points = calculator.calculatePoints(m, tournamentDate);
        pointsMap.merge(p1, p1Points, Integer::sum);

        int p1Place = placementCalculator.calculatePlace(m);
        if (p1Place != 0) {
            placeMap.put(p1, p1Place);
        }

        // PLAYER 2 (reverse)
        Match reversed = m.reverse();

        int p2Points = calculator.calculatePoints(reversed, tournamentDate);
        pointsMap.merge(p2, p2Points, Integer::sum);

        int p2Place = placementCalculator.calculatePlace(reversed);
        if (p2Place != 0) {
            placeMap.put(p2, p2Place);
        }
    }

    private boolean isCompletedMatch(Match m) {
        return m.getStatus() != null
                && m.getStatus().toLowerCase().contains("заверш")
                && (m.getScore1() + m.getScore2() > 0);
    }

    private String normalize(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replace("\u00A0", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }




    // В parseDate заменить DATE_FORMAT на DateConstants.TOURNAMENT_DATE_FORMAT:
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