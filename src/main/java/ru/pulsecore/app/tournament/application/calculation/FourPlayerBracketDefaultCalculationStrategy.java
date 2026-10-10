package ru.pulsecore.app.tournament.application.calculation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.calculation.league.place.PlacePointsCalculatorFactory;
import ru.pulsecore.app.tournament.domain.MatchCalculationStrategy;
import ru.pulsecore.app.tournament.domain.PlacePointsCalculator;
import ru.pulsecore.app.tournament.domain.enums.MatchNumber;
import ru.pulsecore.app.tournament.domain.enums.Place;
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
public class FourPlayerBracketDefaultCalculationStrategy implements MatchCalculationStrategy {


    private final PlacePointsCalculatorFactory placePointsCalculatorFactory;

    @Override
    public StrategyType getType() {
        return StrategyType.FOUR_PL_BRACKET_DEFAULT;
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
            MatchNumber matchNumber = MatchNumber.fromNumber(m.getSortNumber());

            switch (matchNumber) {
                case FINAL -> {
                    places.put(StringUtils.normalizeSearch(m.winnerOf()), Place.FIRST.getValue());
                    places.put(StringUtils.normalizeSearch(m.loserOf()), Place.SECOND.getValue());
                }
                case LOWER_MESH_3_GAME_2 ->
                        places.put(StringUtils.normalizeSearch(m.loserOf()), Place.THIRD.getValue());
                case LOWER_MESH_3_GAME_1 ->
                        places.put(StringUtils.normalizeSearch(m.loserOf()), Place.FOURTH.getValue());
                default -> {
                }
            }
        }
        return places;
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