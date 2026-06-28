package ru.pulsecore.app.modules.player.service.analytic.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
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

    private static final String CACHE_ANALYTICS = "analytics";
    private static final String CACHE_MONTHLY = "monthly_income";
    private static final String CACHE_DAILY = "daily_income";
    private static final String CACHE_BEST_TIME = "best_time";

    private static final String KEY_PLAYER = "#playerId";

    private final PlayerService playerService;
    private final LeagueAnalyticsService leagueAnalyticsService;
    private final PlayerIncomeService playerIncomeService;
    private final PlayerAnalyticsRepository playerAnalyticsRepository;

    @Cacheable(value = CACHE_ANALYTICS, key = KEY_PLAYER + " + ':' + #days")
    public AnalyticsResponse getAnalytics(UUID playerId, int days) {
        Player player = playerService.getById(playerId);
        return leagueAnalyticsService.getAnalytics(player, days);
    }

    @Cacheable(value = CACHE_MONTHLY, key = KEY_PLAYER + " + ':' + #year")
    public MonthlyIncomeResponse getMonthlyIncome(UUID playerId, int year) {
        Player player = playerService.getById(playerId);
        return playerIncomeService.getMonthlyIncome(player, year);
    }

    @Cacheable(value = CACHE_DAILY, key = KEY_PLAYER + " + ':' + #year + ':' + #month")
    public DailyIncomeResponse getDailyIncome(UUID playerId, int year, int month) {
        Player player = playerService.getById(playerId);
        return playerIncomeService.getDailyIncome(player, year, month);
    }

    @Cacheable(value = CACHE_BEST_TIME, key = KEY_PLAYER + " + ':' + #start + ':' + #end")
    public List<BestTimeResponse> getBestTime(UUID playerId, LocalDate start, LocalDate end) {
        return playerAnalyticsRepository.getBestTime(playerId, start, end);
    }
}