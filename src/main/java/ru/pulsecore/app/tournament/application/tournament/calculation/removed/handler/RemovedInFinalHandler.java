package ru.pulsecore.app.tournament.application.tournament.calculation.removed.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.tournament.calculation.DefaultMatchCalculationStrategy;
import ru.pulsecore.app.tournament.domain.RemovedPlayerHandler;
import ru.pulsecore.app.tournament.domain.RemovedStage;
import ru.pulsecore.app.tournament.domain.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.TournamentContext;

@Component
@RequiredArgsConstructor
public class RemovedInFinalHandler implements RemovedPlayerHandler {

    private final DefaultMatchCalculationStrategy defaultStrategy;

    @Override
    public RemovedStage getStage() {
        return RemovedStage.FINAL;
    }

    @Override
    public MatchProcessingResult handle(TournamentContext ctx) {
        return defaultStrategy.process(ctx);
    }
}