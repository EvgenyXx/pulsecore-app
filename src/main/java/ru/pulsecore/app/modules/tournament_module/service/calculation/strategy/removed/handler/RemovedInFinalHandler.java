package ru.pulsecore.app.modules.tournament_module.service.calculation.strategy.removed.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.tournament_module.service.calculation.strategy.DefaultMatchCalculationStrategy;
import ru.pulsecore.app.modules.tournament_module.service.calculation.strategy.removed.RemovedPlayerHandler;
import ru.pulsecore.app.modules.tournament_module.service.calculation.strategy.removed.RemovedStage;
import ru.pulsecore.app.modules.tournament_module.domain.MatchProcessingResult;
import ru.pulsecore.app.modules.tournament_module.domain.TournamentContext;

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