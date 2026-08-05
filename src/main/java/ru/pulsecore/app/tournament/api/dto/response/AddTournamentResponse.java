package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.pulsecore.app.shared.dto.ResultDto;

import java.util.List;

@Data
@Builder
public class AddTournamentResponse {
    private String message;
    private Long tournamentId;
    private int resultsCount;
    private List<ResultDto> results;
}