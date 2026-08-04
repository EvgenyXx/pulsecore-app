package ru.pulsecore.app.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.pulsecore.app.modules.tournament_module.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.entity.TournamentLinkStatus;

@Data
@AllArgsConstructor
public class TournamentLinkResult {

    private TournamentLinkStatus status;
    private ParsedResult parsed;

}