package ru.pulsecore.app.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.player.api.dto.response.AnalyticsResponse;
import ru.pulsecore.app.player.api.dto.response.BestTimeResponse;
import ru.pulsecore.app.player.api.dto.response.DailyIncomeResponse;
import ru.pulsecore.app.player.api.dto.response.MonthlyIncomeResponse;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.application.analytics.AnalyticsFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsFacade analyticsFacade;


    @GetMapping(TournamentApi.ANALYTICS)
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(analyticsFacade.getAnalytics(principal.playerId(), days));
    }

    @GetMapping(TournamentApi.MONTHLY_INCOME)
    public ResponseEntity<MonthlyIncomeResponse> getMonthlyIncome(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(analyticsFacade.getMonthlyIncome(principal.playerId(), year));
    }

    @GetMapping(TournamentApi.DAILY_INCOME)
    public ResponseEntity<DailyIncomeResponse> getDailyIncome(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(analyticsFacade.getDailyIncome(principal.playerId(), year, month));
    }

    @GetMapping(TournamentApi.BEST_TIME)
    public ResponseEntity<List<BestTimeResponse>> getBestTime(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
        return ResponseEntity.ok(analyticsFacade.getBestTime(principal.playerId(), start, end));
    }



}