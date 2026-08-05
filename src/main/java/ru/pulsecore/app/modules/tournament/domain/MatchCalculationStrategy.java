package ru.pulsecore.app.modules.tournament.domain;

public interface MatchCalculationStrategy {

    StrategyType getType() ;

    MatchProcessingResult process(TournamentContext ctx);
}