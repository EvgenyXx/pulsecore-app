package ru.pulsecore.app.modules.tournament.domain;

import ru.pulsecore.app.core.dto.ResultDto;

import java.util.List;

public record ParsedResult(Long tournamentId, List<ResultDto> results, TournamentStatus status, double nightBonus,
                           boolean hasRemoved, boolean isFinalRemoved, String league) {

    public boolean isFinished() {
        return status != null && status.isFinished();
    }
}