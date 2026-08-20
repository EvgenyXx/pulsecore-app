package ru.pulsecore.app.notification.client;


import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Клиент модуля игроков
 */
public interface PlayerClient {
    boolean togglePushEnabled(UUID playerId);
    boolean isPushEnabled(UUID playerId);

    PlayerData getPlayer(UUID playerId);

    List<PlayerData> getPlayers(Set<UUID> playerIds);
}