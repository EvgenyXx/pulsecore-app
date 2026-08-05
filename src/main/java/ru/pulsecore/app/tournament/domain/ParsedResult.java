package ru.pulsecore.app.tournament.domain;

import ru.pulsecore.app.shared.dto.response.ResultDto;

import java.util.List;

public record ParsedResult(Long tournamentId, List<ResultDto> results, TournamentStatus status,
                           double nightBonus,
                           boolean hasRemoved,
                           boolean isFinalRemoved,
                           String league,
                           String time) {

    public boolean isFinished() {
        return status != null && status.isFinished();
    }
}