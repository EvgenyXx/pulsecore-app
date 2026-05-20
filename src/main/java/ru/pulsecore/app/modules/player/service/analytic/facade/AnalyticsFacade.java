package ru.pulsecore.app.modules.player.service.analytic.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.api.dto.AnalyticsResponse;
import ru.pulsecore.app.modules.player.api.dto.DailyIncomeResponse;
import ru.pulsecore.app.modules.player.api.dto.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.analytic.ai.AiAssistantFacade;
import ru.pulsecore.app.modules.player.service.analytic.income.PlayerIncomeService;
import ru.pulsecore.app.modules.player.service.analytic.league.LeagueAnalyticsService;
import ru.pulsecore.app.modules.player.service.player.PlayerService;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsFacade {

    private final PlayerService playerService;
    private final LeagueAnalyticsService leagueAnalyticsService;
    private final PlayerIncomeService playerIncomeService;
    private final AiAssistantFacade aiAssistant;

    // ── Аналитика ─────────────────────────────
    public AnalyticsResponse getAnalytics(UUID playerId, int days) {
        Player player = playerService.getById(playerId);
        return leagueAnalyticsService.getAnalytics(player, days);
    }

    public MonthlyIncomeResponse getMonthlyIncome(UUID playerId, int year) {
        Player player = playerService.getById(playerId);
        return playerIncomeService.getMonthlyIncome(player, year);
    }

    public DailyIncomeResponse getDailyIncome(UUID playerId, int year, int month) {
        Player player = playerService.getById(playerId);
        return playerIncomeService.getDailyIncome(player, year, month);
    }

    // ── AI-ассистент ──────────────────────────
    public Map<String, String> chat(UUID playerId, String question) {
        Player player = playerService.getById(playerId);
        String answer = aiAssistant.chat(player, question);
        return Map.of("answer", answer);
    }


}