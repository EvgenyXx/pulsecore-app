package ru.pulsecore.app.tournament.infrastructure.client;


import ru.pulsecore.app.player.api.dto.response.SubscriptionInfoDto;
import ru.pulsecore.app.shared.dto.response.PlayerData;


import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PlayerClient {

    PlayerData getPlayerById(UUID playerId);

    List<PlayerData> searchByName(String query);

    PlayerData findByName(String fullName);


    SubscriptionInfoDto getSubscriptionInfo(UUID playerId);

    List<PlayerData> getPlayerDataByIds(Set<UUID> playerIds);

    List<PlayerData>getAllActivePlayers();

}
