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



    //todo удалить
//    @GetMapping(TournamentApi.SEARCH)
//    public ResponseEntity<List<TournamentDto>> searchTournaments(
//            @RequestParam(TournamentApi.PARAM_DATE) String date,
//            @RequestParam(required = false) String endDate) {
//        return ResponseEntity.ok(tournamentFacade.searchTournaments(date, endDate));
//    }






}