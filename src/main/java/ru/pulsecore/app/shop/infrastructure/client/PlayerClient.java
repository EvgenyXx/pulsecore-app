package ru.pulsecore.app.shop.infrastructure.client;

import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.UUID;

public interface PlayerClient {

    PlayerData getPlayer(UUID playerId);
}
