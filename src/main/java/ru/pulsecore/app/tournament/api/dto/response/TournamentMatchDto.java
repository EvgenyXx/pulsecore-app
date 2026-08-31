package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import ru.pulsecore.app.tournament.domain.entity.MatchStage;

import java.time.LocalDateTime;


@Builder
public record TournamentMatchDto(
        MatchStage stage,
        String player1Name,
        String player2Name,
        String score,
        String winnerName,
        LocalDateTime playedAt
) {}