package ru.pulsecore.app.shared.api.tournament;

import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TournamentPlayerClient {

    void deleteByPlayerId(UUID playerId);

    List<PriorityLeagueResponse> getLeagues(Set<UUID> playerIds);
}
