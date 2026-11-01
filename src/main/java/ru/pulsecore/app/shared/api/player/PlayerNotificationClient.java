package ru.pulsecore.app.shared.api.player;


import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Клиент модуля игроков
 */
public interface PlayerNotificationClient {

    boolean togglePushEnabled(UUID playerId);

    boolean isPushEnabled(UUID playerId);

    PlayerData getPlayer(UUID playerId);

    List<PlayerData> getPlayers(Set<UUID> playerIds);
}