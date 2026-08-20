package ru.pulsecore.app.player.client;

import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TournamentClient {

    void deleteByPlayerId(UUID playerId);

    List<PriorityLeagueResponse> getLeagues(Set<UUID> playerIds);
}
