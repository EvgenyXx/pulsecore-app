package ru.pulsecore.app.player.application.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.CacheNames;
import ru.pulsecore.app.player.api.dto.response.AnalyticsResponse;
import ru.pulsecore.app.player.api.dto.response.BestTimeResponse;
import ru.pulsecore.app.player.api.dto.response.DailyIncomeResponse;
import ru.pulsecore.app.player.api.dto.response.MonthlyIncomeResponse;
import ru.pulsecore.app.player.entity.Player;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerAnalyticsRepository;
import ru.pulsecore.app.player.application.player.PlayerService;

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