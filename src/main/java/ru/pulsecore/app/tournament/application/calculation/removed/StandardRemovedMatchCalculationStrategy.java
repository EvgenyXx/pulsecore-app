package ru.pulsecore.app.tournament.application.calculation.removed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.calculation.StandardDefaultMatchCalculationStrategy;

import ru.pulsecore.app.tournament.domain.*;
import ru.pulsecore.app.tournament.domain.enums.RemovedStage;
import ru.pulsecore.app.tournament.domain.enums.StrategyType;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;

@Component
@RequiredArgsConstructor
@Slf4j
public class StandardRemovedMatchCalculationStrategy implements MatchCalculationStrategy {

    private final StandardDefaultMatchCalculationStrategy defaultStrategy;
    private final RemovedHandlerRegistry registry;

    @Override
    public StrategyType getType() {
        return StrategyType.STANDARD_REMOVED;
    }

    @Override
    public MatchProcessingResult process(TournamentContext ctx) {

        RemovedStage stage = ctx.getRemovedStage();

        if (stage == null || stage == RemovedStage.NONE) {
            if (log.isDebugEnabled()) {
                log.debug("Removed strategy → fallback to DEFAULT");
            }
            return defaultStrategy.process(ctx);
        }

        RemovedPlayerHandler handler = registry.get(stage);

        if (handler == null) {
            throw new IllegalStateException("No handler for stage: " + stage);
        }

        return handler.handle(ctx);
    }
}