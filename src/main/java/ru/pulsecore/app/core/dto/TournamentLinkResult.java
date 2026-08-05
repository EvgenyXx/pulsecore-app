package ru.pulsecore.app.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament.infrastructure.persistence.entity.TournamentLinkStatus;

@Data
@AllArgsConstructor
public class TournamentLinkResult {

    private TournamentLinkStatus status;
    private ParsedResult parsed;

}