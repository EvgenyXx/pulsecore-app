package ru.pulsecore.app.modules.player.api;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.core.dto.TopPlayerProjection;
import ru.pulsecore.app.modules.player.api.dto.DashboardResponse;
import ru.pulsecore.app.modules.player.api.dto.SumResponse;
import ru.pulsecore.app.modules.player.api.dto.TopWeekResponse;
import ru.pulsecore.app.modules.player.service.PlayerStatsService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PlayerStatsController {

    private final PlayerStatsService playerStatsService;//5

    @GetMapping(PlayerApi.DASHBOARD)
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(playerStatsService.getDashboard(id));
    }

    @GetMapping(PlayerApi.TOP_WEEK)
    public ResponseEntity<List<TopPlayerProjection>> getTopWeek() {
        return ResponseEntity.ok(playerStatsService.getTopPlayers());
    }

    @GetMapping(PlayerApi.TOP_WEEK_POSITION)
    public ResponseEntity<TopWeekResponse> getTopWeekPosition(@PathVariable UUID id) {
        return ResponseEntity.ok(playerStatsService.getTopWithPosition(id));
    }

    @GetMapping(PlayerApi.TOP_WEEK_POSITION_BY_LEAGUE)
    public ResponseEntity<TopWeekResponse> getTopWeekPositionByLeague(@PathVariable UUID id, @PathVariable String league) {
        return ResponseEntity.ok(playerStatsService.getTopWithPositionByLeague(id, league));
    }

    @GetMapping(PlayerApi.SUM)
    public ResponseEntity<SumResponse> getSumById(
            @PathVariable UUID id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(playerStatsService.getSum(id, start, end));
    }
}