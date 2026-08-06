
package ru.pulsecore.app.tournament.application.top;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.PlayerSummaryResponse;

import ru.pulsecore.app.player.api.dto.response.TopLeagueResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerSummaryFacade {

    private final PlayerSummaryService playerSummaryService;
    private final TopPeriodService topPeriodService;



    public PlayerSummaryResponse getDashboard(UUID id) {
        return playerSummaryService.getDashboard(id);
    }

    public TopLeagueResponse getTopAll(String period, UUID playerId) {
        return topPeriodService.getTopAllLeagues(period, playerId);
    }

    public TopLeagueResponse getTopByLeague(String period, String league, UUID playerId) {
        return topPeriodService.getTopByLeague(period, league, playerId);
    }


}