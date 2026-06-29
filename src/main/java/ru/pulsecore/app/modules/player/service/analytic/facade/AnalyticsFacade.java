package ru.pulsecore.app.modules.player.service.analytic.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.config.CacheNames;
import ru.pulsecore.app.modules.player.api.dto.analytics.AnalyticsResponse;
import ru.pulsecore.app.modules.player.api.dto.analytics.BestTimeResponse;
import ru.pulsecore.app.modules.player.api.dto.analytics.DailyIncomeResponse;
import ru.pulsecore.app.modules.player.api.dto.analytics.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerAnalyticsRepository;
import ru.pulsecore.app.modules.player.service.analytic.income.PlayerIncomeService;
import ru.pulsecore.app.modules.player.service.analytic.league.LeagueAnalyticsService;
import ru.pulsecore.app.modules.player.service.player.PlayerService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsFacade {

    private final PlayerService playerService;
    private final LeagueAnalyticsService leagueAnalyticsService;
    private final PlayerIncomeService playerIncomeService;
    private final PlayerAnalyticsRepository playerAnalyticsRepository;

    @Cacheable(value = CacheNames.ANALYTICS, key = CacheNames.KEY_PLAYER_ID + " + ':' + #days")
    public AnalyticsResponse getAnalytics(UUID playerId, int days) {
        Player player = playerService.getById(playerId);
        return leagueAnalyticsService.getAnalytics(player, days);
    }

    @Cacheable(value = CacheNames.MONTHLY_INCOME, key = CacheNames.KEY_PLAYER_ID + " + ':' + #year")
    public MonthlyIncomeResponse getMonthlyIncome(UUID playerId, int year) {
        Player player = playerService.getById(playerId);
        return playerIncomeService.getMonthlyIncome(player, year);
    }

    @Cacheable(value = CacheNames.DAILY_INCOME, key = CacheNames.KEY_PLAYER_ID + " + ':' + #year + ':' + #month")
    public DailyIncomeResponse getDailyIncome(UUID playerId, int year, int month) {
        Player player = playerService.getById(playerId);
        return playerIncomeService.getDailyIncome(player, year, month);
    }

    @Cacheable(value = CacheNames.BEST_TIME, key = CacheNames.KEY_PLAYER_ID + " + ':' + #start + ':' + #end")
    public List<BestTimeResponse> getBestTime(UUID playerId, LocalDate start, LocalDate end) {
        return playerAnalyticsRepository.getBestTime(playerId, start, end);
    }
}