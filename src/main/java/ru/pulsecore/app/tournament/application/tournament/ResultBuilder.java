package ru.pulsecore.app.tournament.application.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.ResultDto;
import ru.pulsecore.app.tournament.domain.BonusCalculator;
import ru.pulsecore.app.tournament.domain.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.TournamentContext;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResultBuilder {

    private final BonusCalculator bonusCalculator;

    public List<ResultDto> build(MatchProcessingResult matchResult,
                                 TournamentContext ctx) {

        List<ResultDto> results = new ArrayList<>();

        for (String player : matchResult.getPointsMap().keySet()) {

            int place = matchResult.getPlaceMap().getOrDefault(player, 0);
            int bonus = bonusCalculator.getBonus(place);

            int base = matchResult.getPointsMap().get(player) + bonus;
            int total = base + (int) ctx.getNightBonus();

            results.add(new ResultDto(
                    null,  // id — будет проставлен после сохранения
                    player,
                    place,
                    bonus,
                    total,
                    ctx.getDate()
            ));
        }

        return results;
    }
}