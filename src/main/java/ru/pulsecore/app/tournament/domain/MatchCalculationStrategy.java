package ru.pulsecore.app.tournament.domain;

public interface MatchCalculationStrategy {

    StrategyType getType() ;

    MatchProcessingResult process(TournamentContext ctx);
}