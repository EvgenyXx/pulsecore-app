package ru.pulsecore.app.tournament.domain;

import ru.pulsecore.app.tournament.domain.enums.RemovedStage;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;

public interface RemovedPlayerHandler {

    RemovedStage getStage();

    MatchProcessingResult handle(TournamentContext ctx);
}