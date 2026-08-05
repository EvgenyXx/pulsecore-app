package ru.pulsecore.app.tournament.api.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.api.TournamentApi;

import ru.pulsecore.app.tournament.application.tournament.TournamentFacade;

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