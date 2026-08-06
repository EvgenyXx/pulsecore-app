package ru.pulsecore.app.tournament.infrastructure.client;


import ru.pulsecore.app.player.api.dto.response.SubscriptionInfoDto;
import ru.pulsecore.app.shared.dto.response.NotificationInfo;
import ru.pulsecore.app.shared.dto.response.PlayerData;


import java.util.List;
import java.util.UUID;

public interface PlayerClient {

    PlayerData getPlayerById(UUID playerId);

    List<PlayerData> searchByName(String query);

    PlayerData findByName(String fullName);

    NotificationInfo getNotificationInfo(UUID playerId);

    SubscriptionInfoDto getSubscriptionInfo(UUID playerId);

}
