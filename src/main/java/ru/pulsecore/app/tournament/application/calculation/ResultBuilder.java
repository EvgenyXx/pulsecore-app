package ru.pulsecore.app.tournament.application.calculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.ResultDto;
import ru.pulsecore.app.tournament.application.calculation.league.BonusCalculator;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResultBuilder {

    private final BonusCalculator bonusCalculator;

    public List<ResultDto> build(MatchProcessingResult matchResult,
                                 TournamentContext ctx) {

        List<ResultDto> results = new ArrayList<>();

        boolean isNewFormat = "4pl_new".equals(ctx.getTypeId());

        for (String player : matchResult.getPointsMap().keySet()) {

            int place = matchResult.getPlaceMap().getOrDefault(player, 0);

            // Бонус за место — только для standart. Для 4pl_new очки уже включают место.
            int bonus = isNewFormat ? 0 : bonusCalculator.getBonus(place);

            int base = matchResult.getPointsMap().get(player) + bonus;
            int total = base + (int) ctx.getNightBonus();

            results.add(new ResultDto(
                    null,
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