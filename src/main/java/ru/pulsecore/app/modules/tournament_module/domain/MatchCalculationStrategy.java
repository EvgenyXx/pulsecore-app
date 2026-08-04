package ru.pulsecore.app.modules.tournament_module.domain;

public interface MatchCalculationStrategy {

    StrategyType getType() ;

    MatchProcessingResult process(TournamentContext ctx);
}