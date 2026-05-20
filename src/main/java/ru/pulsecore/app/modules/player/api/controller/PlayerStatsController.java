// ==================== PlayerStatsController.java ====================
package ru.pulsecore.app.modules.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.core.dto.TopPlayerProjection;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.DashboardResponse;
import ru.pulsecore.app.modules.player.api.dto.SumResponse;
import ru.pulsecore.app.modules.player.api.dto.TopWeekResponse;
import ru.pulsecore.app.modules.player.service.analytic.facade.PlayerStatsFacade;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.time.LocalDate;
import java.util.List;
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

    // ── Топ недели ──
    @GetMapping(PlayerApi.TOP_WEEK)
    public ResponseEntity<List<TopPlayerProjection>> getTopWeek() {
        return ResponseEntity.ok(facade.getTopPlayers());
    }

    @GetMapping(PlayerApi.TOP_WEEK_POSITION)
    public ResponseEntity<TopWeekResponse> getTopWeekPosition(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(facade.getTopWithPosition(principal.playerId()));
    }

    @GetMapping(PlayerApi.TOP_WEEK_POSITION_BY_LEAGUE)
    public ResponseEntity<TopWeekResponse> getTopWeekPositionByLeague( @CurrentPlayer PlayerPrincipal principal,
                                                                      @PathVariable String league) {
        return ResponseEntity.ok(facade.getTopWithPositionByLeague(principal.playerId(), league));
    }

    // ── Топ месяца ──
    @GetMapping(PlayerApi.TOP_MONTH)
    public ResponseEntity<List<TopPlayerProjection>> getTopMonth() {
        return ResponseEntity.ok(facade.getTopPlayersMonth());
    }

    @GetMapping(PlayerApi.TOP_MONTH_POSITION)
    public ResponseEntity<TopWeekResponse> getTopMonthPosition( @CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(facade.getTopWithPositionMonth(principal.playerId()));
    }

    @GetMapping(PlayerApi.TOP_MONTH_POSITION_BY_LEAGUE)
    public ResponseEntity<TopWeekResponse> getTopMonthPositionByLeague( @CurrentPlayer PlayerPrincipal principal,
                                                                       @PathVariable String league) {
        return ResponseEntity.ok(facade.getTopWithPositionByLeagueMonth(principal.playerId(), league));
    }

    @GetMapping(PlayerApi.SUM)
    public ResponseEntity<SumResponse> getSumById(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(facade.getSum(principal.playerId(), start, end));
    }
}