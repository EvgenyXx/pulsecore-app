package ru.pulsecore.app.modules.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.AnalyticsResponse;
import ru.pulsecore.app.modules.player.api.dto.DailyIncomeResponse;
import ru.pulsecore.app.modules.player.api.dto.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.player.service.analytic.AnalyticsFacade;
import ru.pulsecore.app.modules.shared.security.CurrentPlayer;
import ru.pulsecore.app.modules.shared.security.PlayerPrincipal;

import java.util.Map;
import java.util.UUID;

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
            @PathVariable UUID id,
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(analyticsFacade.getMonthlyIncome(id, year));
    }

    @GetMapping(PlayerApi.DAILY_INCOME)
    public ResponseEntity<DailyIncomeResponse> getDailyIncome(
            @PathVariable UUID id,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(analyticsFacade.getDailyIncome(id, year, month));
    }

    // ── AI-ассистент ──────────────────────────
    @PostMapping(PlayerApi.CHAT)
    public ResponseEntity<Map<String, String>> chat(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(analyticsFacade.chat(principal.playerId(), request.get("question")));
    }

    @GetMapping(PlayerApi.WEEKLY_ANALYSIS)
    public ResponseEntity<Map<String, String>> getWeeklyAnalysis(
            @CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(analyticsFacade.weeklyAnalysis(principal.playerId()));
    }
}