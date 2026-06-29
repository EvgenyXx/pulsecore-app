// ==================== PlayerStatsController.java ====================
package ru.pulsecore.app.modules.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.dashboard.DashboardResponse;
import ru.pulsecore.app.modules.player.api.dto.sum.SumResponse;
import ru.pulsecore.app.modules.player.api.dto.top.TopLeagueResponse;
import ru.pulsecore.app.modules.player.service.analytic.facade.PlayerStatsFacade;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PlayerStatsController {

    private final PlayerStatsFacade facade;

    @GetMapping(PlayerApi.DASHBOARD)
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(facade.getDashboard(id));
    }

    @GetMapping(PlayerApi.TOP_ALL)
    public ResponseEntity<TopLeagueResponse> getTopAll(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable String period) {
        return ResponseEntity.ok(facade.getTopAll(period.toUpperCase(), principal.playerId()));
    }

    @GetMapping(PlayerApi.TOP_BY_LEAGUE)
    public ResponseEntity<TopLeagueResponse> getTopByLeague(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable String period,
            @PathVariable String league) {
        return ResponseEntity.ok(facade.getTopByLeague(period.toUpperCase(), league, principal.playerId()));
    }

    // ==================== PlayerStatsController.java — поправить ====================
    @GetMapping(PlayerApi.SUM)
    public ResponseEntity<SumResponse> getSumById(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(facade.getSum(principal.playerId(), start, end, page, size));
    }
}