package ru.pulsecore.app.tournament.infrastructure.repository.projection;

import ru.pulsecore.app.tournament.domain.enums.MatchStage;

import java.time.LocalDateTime;

public interface TournamentMatchProjection {
    MatchStage getStage();
    String getPlayer1Name();
    String getPlayer2Name();
    String getScore();
    String getWinnerName();
    LocalDateTime getPlayedAt();
}