package ru.pulsecore.app.tournament.application.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.TopLeagueResponse;
import ru.pulsecore.app.shared.config.CacheNames;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.application.mapping.TopLeagueMapper;
import ru.pulsecore.app.tournament.domain.entity.TopPlayersView;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.repository.TopPlayersViewRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopPeriodService {

    private final TopPlayersViewRepository repository;
    private final PlayerClient playerClient;
    private final TopLeagueMapper mapper;

    @Cacheable(value = CacheNames.TOP_ALL, key = CacheNames.KEY_PERIOD)
    public TopLeagueResponse getTopAllLeagues(String period, UUID playerId) {
        List<TopPlayersView> all = repository.findByPeriodOrderByTotalDesc(period);
        return mapper.toLeagueResponse(all, playerId);
    }

    @Cacheable(value = CacheNames.TOP_LEAGUE, key = CacheNames.KEY_PERIOD_LEAGUE)
    public TopLeagueResponse getTopByLeague(String period, String league, UUID playerId) {
        PlayerData player = playerClient.getPlayerById(playerId);
        List<TopPlayersView> players = repository.findByPeriodAndPrimaryLeagueOrderByTotalDesc(period, league);

        if (!league.equals(player.primaryLeague())) {
            return mapper.toLeagueResponseWithoutPlayer(players);
        }

        return mapper.toLeagueResponse(players, playerId);
    }
}