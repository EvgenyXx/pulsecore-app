// ==================== PlayerStatsFacade.java ====================
package ru.pulsecore.app.modules.player_modeles.application.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player_modeles.api.dto.response.DashboardResponse;
import ru.pulsecore.app.modules.player_modeles.api.dto.response.SumResponse;
import ru.pulsecore.app.modules.player_modeles.api.dto.response.TopLeagueResponse;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerStatsFacade {

    private final DashboardService dashboardService;
    private final TopPeriodService topPeriodService;
    private final SumService sumService;


    public DashboardResponse getDashboard(UUID id) {
        return dashboardService.getDashboard(id);
    }

    public TopLeagueResponse getTopAll(String period, UUID playerId) {
        return topPeriodService.getTopAllLeagues(period, playerId);
    }

    public TopLeagueResponse getTopByLeague(String period, String league, UUID playerId) {
        return topPeriodService.getTopByLeague(period, league, playerId);
    }

    public SumResponse getSum(UUID id, LocalDate start, LocalDate end, int page, int size) {
        return sumService.getSum(id, start, end, page, size);
    }
}