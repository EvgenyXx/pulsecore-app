package ru.pulsecore.app.modules.tournament.application.tournament.calculation.removed.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.tournament.application.tournament.calculation.DefaultMatchCalculationStrategy;
import ru.pulsecore.app.modules.tournament.domain.RemovedPlayerHandler;
import ru.pulsecore.app.modules.tournament.domain.RemovedStage;
import ru.pulsecore.app.modules.tournament.domain.MatchProcessingResult;
import ru.pulsecore.app.modules.tournament.domain.TournamentContext;

@Component
@RequiredArgsConstructor
public class RemovedInThirdPlaceHandler implements RemovedPlayerHandler {

    private final DefaultMatchCalculationStrategy defaultStrategy;

    @Override
    public RemovedStage getStage() {
        return RemovedStage.THIRD_PLACE;
    }

    @Override
    public MatchProcessingResult handle(TournamentContext ctx) {

        return defaultStrategy.process(ctx);
    }
}