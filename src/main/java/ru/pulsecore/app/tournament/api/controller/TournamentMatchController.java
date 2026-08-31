package ru.pulsecore.app.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.response.TournamentMatchDto;
import ru.pulsecore.app.tournament.application.compare.TournamentMatchPersistence;
import java.util.List;

@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class TournamentMatchController {

    private final TournamentMatchPersistence matchService;

    @GetMapping(TournamentApi.MATCHES_BY_RESULT)
    public List<TournamentMatchDto> getMatchesByResultId(@PathVariable Long resultId) {
        return matchService.getMatchesByResultId(resultId);
    }

}