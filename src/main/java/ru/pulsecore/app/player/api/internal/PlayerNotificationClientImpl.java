package ru.pulsecore.app.player.api.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.api.player.PlayerNotificationClient;
import ru.pulsecore.app.player.application.player.PlayerPushService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Реализация контракта для модуля уведомлений.
 * Предоставляет доступ к данным игроков и управлению push-уведомлениями.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerNotificationClientImpl implements PlayerNotificationClient {


    private final PlayerSearchService searchService;
    private final PlayerPushService playerPushService;



    @Override
    public boolean togglePushEnabled(UUID playerId) {
      return playerPushService.togglePushEnabled(playerId);
    }

    @Override
    public boolean isPushEnabled(UUID playerId) {
        return searchService.getById(playerId).isPushEnabled();
    }

    @Override
    public PlayerData getPlayer(UUID playerId) {
        return searchService.getPlayerById(playerId);
    }

    @Override
    public List<PlayerData> getPlayers(Set<UUID> playerIds) {
       return searchService.getPlayersIds(playerIds);
    }
}
