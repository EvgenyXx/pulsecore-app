package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

import ru.pulsecore.app.tournament.domain.entity.MatchStage;

public interface PlayerH2HProjection {
    MatchStage getStage();
    Long getTotalMatches();
    Long getPlayer1Wins();
    Long getPlayer2Wins();
}