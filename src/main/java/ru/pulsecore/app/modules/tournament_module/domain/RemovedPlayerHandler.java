package ru.pulsecore.app.modules.tournament_module.domain;

public interface RemovedPlayerHandler {

    RemovedStage getStage();

    MatchProcessingResult handle(TournamentContext ctx);
}