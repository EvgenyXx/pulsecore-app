package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.api.dto.response.PlayerSummaryResponse;
import ru.pulsecore.app.player.api.dto.response.TopLeagueResponse;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.application.leaderboard.PlayerSummaryFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.util.UUID;

@Tag(name = "Tournament", description = "Главная страница, зал славы и топы")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class PlayerSummaryController {

    private final PlayerSummaryFacade facade;

    @Operation(summary = "Главная страница игрока: последний результат, ближайшие турниры, подписка")
    @GetMapping(TournamentApi.DASHBOARD)
    public ResponseEntity<PlayerSummaryResponse> getDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(facade.getDashboard(id));
    }

    @Operation(summary = "Топ всех лиг за период")
    @GetMapping(TournamentApi.TOP_ALL)
    public ResponseEntity<TopLeagueResponse> getTopAll(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable String period) {
        return ResponseEntity.ok(facade.getTopAll(period.toUpperCase(), principal.playerId()));
    }

    @Operation(summary = "Топ по конкретной лиге за период")
    @GetMapping(TournamentApi.TOP_BY_LEAGUE)
    public ResponseEntity<TopLeagueResponse> getTopByLeague(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable String period,
            @PathVariable String league) {
        return ResponseEntity.ok(facade.getTopByLeague(period.toUpperCase(), league, principal.playerId()));
    }
}