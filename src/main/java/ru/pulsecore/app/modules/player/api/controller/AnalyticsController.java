package ru.pulsecore.app.modules.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.AnalyticsResponse;
import ru.pulsecore.app.modules.player.api.dto.DailyIncomeResponse;
import ru.pulsecore.app.modules.player.api.dto.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.analytic.DailyIncomeService;
import ru.pulsecore.app.modules.player.service.analytic.GigaChatAssistantService;
import ru.pulsecore.app.modules.player.service.analytic.LeagueAverageService;
import ru.pulsecore.app.modules.player.service.analytic.MonthlyIncomeService;
import ru.pulsecore.app.modules.player.service.player.PlayerService;


import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class AnalyticsController {

    private final LeagueAverageService leagueAverageService;
    private final MonthlyIncomeService monthlyIncomeService;
    private final DailyIncomeService dailyIncomeService;

    private final GigaChatAssistantService gigaChatAssistantService;
    private final PlayerService playerService;

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

    @GetMapping(PlayerApi.DAILY_INCOME)
    public ResponseEntity<DailyIncomeResponse> getDailyIncome(
            @PathVariable UUID id,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(dailyIncomeService.getDailyIncome(id, year, month));
    }

    @PostMapping(PlayerApi.CHAT)
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> request) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = (SecurityUser) auth.getPrincipal();
        Player player = playerService.getById(UUID.fromString(user.getPlayerId()));

        String question = request.get("question");
        String answer = gigaChatAssistantService.answer(player, question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}