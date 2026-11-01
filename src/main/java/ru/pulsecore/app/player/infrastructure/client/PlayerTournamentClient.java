package ru.pulsecore.app.player.infrastructure.client;

import ru.pulsecore.app.shared.api.tournament.TournamentPlayerClient;
import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

//подготовка к микро сервисам
public interface PlayerTournamentClient extends TournamentPlayerClient {

    @Override
    void deleteByPlayerId(UUID playerId);

    @Override
    List<PriorityLeagueResponse> getLeagues(Set<UUID> playerIds);
}
