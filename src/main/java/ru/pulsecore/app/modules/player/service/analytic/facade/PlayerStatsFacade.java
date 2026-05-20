package ru.pulsecore.app.modules.player.service.analytic.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TopPlayerProjection;
import ru.pulsecore.app.modules.player.api.dto.DashboardResponse;
import ru.pulsecore.app.modules.player.api.dto.SumResponse;
import ru.pulsecore.app.modules.player.api.dto.TopWeekResponse;
import ru.pulsecore.app.modules.player.service.analytic.dashboard.DashboardService;
import ru.pulsecore.app.modules.player.service.analytic.income.SumService;
import ru.pulsecore.app.modules.player.service.analytic.top.TopWeekService;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerStatsFacade {

    private final DashboardService dashboardService;
    private final TopWeekService topWeekService;
    private final TopMonthService topMonthService;
    private final SumService sumService;

    public DashboardResponse getDashboard(UUID id) {
        return dashboardService.getDashboard(id);
    }

    // ── Топ недели ──
    public List<TopPlayerProjection> getTopPlayers() {
        return topWeekService.getTopPlayers();
    }

    public TopWeekResponse getTopWithPosition(UUID id) {
        return topWeekService.getTopWithPosition(id);
    }

    public TopWeekResponse getTopWithPositionByLeague(UUID id, String league) {
        return topWeekService.getTopWithPositionByLeague(id, league);
    }

    // ── Топ месяца ──
    public List<TopPlayerProjection> getTopPlayersMonth() {
        return topMonthService.getTopPlayers();
    }

    public TopWeekResponse getTopWithPositionMonth(UUID id) {
        return topMonthService.getTopWithPosition(id);
    }

    public TopWeekResponse getTopWithPositionByLeagueMonth(UUID id, String league) {
        return topMonthService.getTopWithPositionByLeague(id, league);
    }

    public SumResponse getSum(UUID id, LocalDate start, LocalDate end) {
        return sumService.getSum(id, start, end);
    }
}