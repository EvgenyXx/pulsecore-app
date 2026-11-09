package ru.pulsecore.app.tournament.domain.model;

import ru.pulsecore.app.shared.dto.response.ResultDto;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.domain.enums.TournamentVersion;

import java.util.List;

public record ParsedResult(Long tournamentId, List<ResultDto> results, TournamentStatus status,
                           double nightBonus,
                           boolean hasRemoved,
                           boolean isFinalRemoved,
                           String league,
                           String time,
                           String date,
                           List<Match> matches,
                           String typeId
) {

    public boolean isFinished() {
        return status != null && status.isFinished();
    }

    /**
     * Версия турнира, вычисляется из typeId.
     * Не хранится как поле, чтобы не было рассинхрона с typeId.
     */
    public TournamentVersion getVersion() {
        return TournamentVersion.fromTypeId(typeId)
                .orElse(TournamentVersion.STANDARD);
    }
}