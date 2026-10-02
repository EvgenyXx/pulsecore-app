package ru.pulsecore.app.player.infrastructure.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shop.infrastructure.client.PlayerClient;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PlayerShopClientImpl implements PlayerClient {

    private final PlayerSearchService playerSearchService;

    @Override
    public PlayerData getPlayer(UUID playerId) {
        return playerSearchService.getPlayerById(playerId);
    }
}
