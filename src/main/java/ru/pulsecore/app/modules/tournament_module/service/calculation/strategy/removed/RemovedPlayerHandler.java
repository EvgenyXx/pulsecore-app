package ru.pulsecore.app.modules.tournament_module.service.calculation.strategy.removed;

import ru.pulsecore.app.modules.tournament_module.domain.MatchProcessingResult;
import ru.pulsecore.app.modules.tournament_module.domain.TournamentContext;

public interface RemovedPlayerHandler {

    RemovedStage getStage();

    MatchProcessingResult handle(TournamentContext ctx);
}