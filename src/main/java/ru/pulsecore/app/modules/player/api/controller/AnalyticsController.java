package ru.pulsecore.app.modules.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.AnalyticsResponse;
import ru.pulsecore.app.modules.player.api.dto.DailyIncomeResponse;
import ru.pulsecore.app.modules.player.api.dto.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.player.service.analytic.facade.AnalyticsFacade;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.util.Map;


@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsFacade analyticsFacade;

    // ── Аналитика ─────────────────────────────
    @GetMapping(PlayerApi.ANALYTICS)
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(analyticsFacade.getAnalytics(principal.playerId(), days));
    }

    @GetMapping(PlayerApi.MONTHLY_INCOME)
    public ResponseEntity<MonthlyIncomeResponse> getMonthlyIncome(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(analyticsFacade.getMonthlyIncome(principal.playerId(), year));
    }

    @GetMapping(PlayerApi.DAILY_INCOME)
    public ResponseEntity<DailyIncomeResponse> getDailyIncome(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(analyticsFacade.getDailyIncome(principal.playerId(), year, month));
    }



}