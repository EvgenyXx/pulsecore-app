package ru.pulsecore.app.modules.player.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.player.api.dto.AnalyticsResponse;
import ru.pulsecore.app.modules.player.api.dto.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.player.service.LeagueAverageService;
import ru.pulsecore.app.modules.player.service.MonthlyIncomeService;

import java.util.UUID;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class AnalyticsController {

    private final LeagueAverageService leagueAverageService;
    private final MonthlyIncomeService monthlyIncomeService;

    @GetMapping(PlayerApi.ANALYTICS)
    public ResponseEntity<AnalyticsResponse> getAnalytics(@RequestParam(defaultValue = "30") int days) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = (SecurityUser) auth.getPrincipal();
        return ResponseEntity.ok(leagueAverageService.getAnalytics(UUID.fromString(user.getPlayerId()), days));
    }

    @GetMapping(PlayerApi.MONTHLY_INCOME)
    public ResponseEntity<MonthlyIncomeResponse> getMonthlyIncome(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(monthlyIncomeService.getMonthlyIncome(id, year));
    }
}