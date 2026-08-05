package ru.pulsecore.app.modules.tournament.infrastructure.client;


import ru.pulsecore.app.modules.shared.dto.NotificationInfo;
import ru.pulsecore.app.modules.shared.dto.PlayerData;


import java.util.List;
import java.util.UUID;

public interface PlayerClient {

    PlayerData getPlayerById(UUID playerId);

    List<PlayerData> searchByName(String query);

    PlayerData findByName(String fullName);

    NotificationInfo getNotificationInfo(UUID playerId);

}
