package ru.pulsecore.app.tournament.application.top;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.CacheNames;
import ru.pulsecore.app.player.api.dto.response.TopLeagueResponse;
import ru.pulsecore.app.player.api.dto.response.TopPlayerDto;
import ru.pulsecore.app.tournament.infrastructure.persistence.entity.TopPlayersView;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TopPlayersViewRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopPeriodService {

    private final TopPlayersViewRepository repository;
    private final PlayerClient playerClient;

    @Cacheable(value = CacheNames.TOP_ALL, key = CacheNames.KEY_PERIOD)
    public TopLeagueResponse getTopAllLeagues(String period, UUID playerId) {
        List<TopPlayersView> all = repository.findByPeriodOrderByTotalDesc(period);
        return buildResponse(all, playerId);
    }

    @Cacheable(value = CacheNames.TOP_LEAGUE, key = CacheNames.KEY_PERIOD_LEAGUE)
    public TopLeagueResponse getTopByLeague(String period, String league, UUID playerId) {
       var player = playerClient.getPlayerById(playerId);

        if (!league.equals(player.primaryLeague())) {
            List<TopPlayersView> top5 = repository.findByPeriodAndPrimaryLeagueOrderByTotalDesc(period, league);
            if (top5.size() > 5) top5 = top5.subList(0, 5);
            return TopLeagueResponse.builder()
                    .top5(top5.stream().map(this::toDto).toList())
                    .playerPosition(0)
                    .playerTournaments(0)
                    .build();
        }

        List<TopPlayersView> all = repository.findByPeriodAndPrimaryLeagueOrderByTotalDesc(period, league);
        return buildResponse(all, playerId);
    }

    private TopLeagueResponse buildResponse(List<TopPlayersView> all, UUID playerId) {
        List<TopPlayersView> top5 = all.size() > 5 ? all.subList(0, 5) : all;

        int position = 0;
        int tournaments = 0;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getPlayerId().equals(playerId)) {
                position = i + 1;
                tournaments = all.get(i).getTournaments().intValue();
                break;
            }
        }

        return TopLeagueResponse.builder()
                .top5(top5.stream().map(this::toDto).toList())
                .playerPosition(position)
                .playerTournaments(tournaments)
                .build();
    }

    private TopPlayerDto toDto(TopPlayersView v) {
        return TopPlayerDto.builder()
                .name(v.getName())
                .tournaments(v.getTournaments())
                .build();
    }
}