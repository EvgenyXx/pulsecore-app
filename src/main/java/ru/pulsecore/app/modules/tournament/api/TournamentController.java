package ru.pulsecore.app.modules.tournament.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.tournament.api.dto.AddTournamentRequest;
import ru.pulsecore.app.modules.tournament.api.dto.AddTournamentResponse;
import ru.pulsecore.app.modules.tournament.api.dto.TournamentSearchResult;
import ru.pulsecore.app.modules.tournament.service.TournamentFacade;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentFacade tournamentFacade;



    @GetMapping(TournamentApi.SEARCH)
    public ResponseEntity<List<TournamentDto>> searchTournaments(
            @RequestParam(TournamentApi.PARAM_DATE) String date,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(tournamentFacade.searchTournaments(date, endDate));
    }

    @PostMapping(TournamentApi.ADD_BATCH)
    public ResponseEntity<List<AddTournamentResponse>> addByUrls(
            @Valid @RequestBody List<AddTournamentRequest> requests) {
        return ResponseEntity.ok(tournamentFacade.addByUrls(requests));
    }

    @PutMapping(TournamentApi.UPDATE_RESULT)
    public ResponseEntity<Map<String, String>> updateResult(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        tournamentFacade.updateResult(
                id,
                body.get(TournamentApi.PARAM_AMOUNT),
                body.get(TournamentApi.PARAM_BONUS));
        return ResponseEntity.ok(Map.of(TournamentApi.RESP_MESSAGE, TournamentApi.RESP_OK));
    }


}