
package ru.pulsecore.app.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.pulsecore.app.player.api.dto.response.PlayerSummaryResponse;
import ru.pulsecore.app.player.api.dto.response.TopLeagueResponse;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.application.top.PlayerSummaryFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;


import java.util.UUID;

@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class PlayerSummaryController {

    private final PlayerSummaryFacade facade;

    @GetMapping(TournamentApi.DASHBOARD)
    public ResponseEntity<PlayerSummaryResponse> getDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(facade.getDashboard(id));
    }

    @GetMapping(TournamentApi.TOP_ALL)
    public ResponseEntity<TopLeagueResponse> getTopAll(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable String period) {
        return ResponseEntity.ok(facade.getTopAll(period.toUpperCase(), principal.playerId()));
    }

    @GetMapping(TournamentApi.TOP_BY_LEAGUE)
    public ResponseEntity<TopLeagueResponse> getTopByLeague(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable String period,
            @PathVariable String league) {
        return ResponseEntity.ok(facade.getTopByLeague(period.toUpperCase(), league, principal.playerId()));
    }



}