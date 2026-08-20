package ru.pulsecore.app.tournament.domain.model;

import ru.pulsecore.app.shared.dto.response.ResultDto;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;

import java.util.List;

public record ParsedResult(Long tournamentId, List<ResultDto> results, TournamentStatus status,
                           double nightBonus,
                           boolean hasRemoved,
                           boolean isFinalRemoved,
                           String league,
                           String time,
                           String date) {

    public boolean isFinished() {
        return status != null && status.isFinished();
    }
}