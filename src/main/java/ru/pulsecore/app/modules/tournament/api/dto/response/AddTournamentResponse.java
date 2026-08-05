package ru.pulsecore.app.modules.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.pulsecore.app.core.dto.ResultDto;

import java.util.List;

@Data
@Builder
public class AddTournamentResponse {
    private String message;
    private Long tournamentId;
    private int resultsCount;
    private List<ResultDto> results;
}