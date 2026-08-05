package ru.pulsecore.app.tournament.domain;

public interface RemovedPlayerHandler {

    RemovedStage getStage();

    MatchProcessingResult handle(TournamentContext ctx);
}