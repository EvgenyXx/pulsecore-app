package ru.pulsecore.app.modules.tournament.calculation.strategy.removed.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.tournament.calculation.strategy.DefaultMatchCalculationStrategy;
import ru.pulsecore.app.modules.tournament.calculation.strategy.removed.RemovedPlayerHandler;
import ru.pulsecore.app.modules.tournament.calculation.strategy.removed.RemovedStage;
import ru.pulsecore.app.modules.tournament.domain.MatchProcessingResult;
import ru.pulsecore.app.modules.tournament.domain.TournamentContext;

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