package ru.pulsecore.app.modules.tournament.api.dto;

import lombok.Builder;
import lombok.Data;
import ru.pulsecore.app.core.dto.TournamentDto;

@Data
@Builder
public class TournamentSearchResult {
    private TournamentDto tournament;
    private boolean saved;
}