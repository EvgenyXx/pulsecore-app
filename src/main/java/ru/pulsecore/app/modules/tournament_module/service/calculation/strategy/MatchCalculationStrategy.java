package ru.pulsecore.app.modules.tournament_module.service.calculation.strategy;

import ru.pulsecore.app.modules.tournament_module.domain.MatchProcessingResult;
import ru.pulsecore.app.modules.tournament_module.domain.TournamentContext;

public interface MatchCalculationStrategy {

    StrategyType getType() ;

    MatchProcessingResult process(TournamentContext ctx);
}