package ru.pulsecore.app.tournament.domain;

import ru.pulsecore.app.tournament.domain.enums.StrategyType;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;

public interface MatchCalculationStrategy {

    StrategyType getType() ;

    MatchProcessingResult process(TournamentContext ctx);
}