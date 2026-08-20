package ru.pulsecore.app.player.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.player.infrastructure.persistence.repository.projection.PlayerDataProjection;
import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPlayerClientImpl implements PlayerClient {

    private final PlayerCommandService  commandService;
    private final PlayerSearchService  searchService;
    private final PlayerRepository  repository;

    @Transactional
    @Override
    public boolean togglePushEnabled(UUID playerId) {
        Player player = searchService.getById(playerId);
        player.setPushEnabled(!player.isPushEnabled());
        commandService.save(player);
        log.info("📲 Push-уведомления {} для игрока {} ({})", player.isPushEnabled() ? "включены" : "отключены", player.getName(), playerId);
        return player.isPushEnabled();
    }

    @Override
    public boolean isPushEnabled(UUID playerId) {
        return searchService.getById(playerId).isPushEnabled();
    }

    @Override
    public PlayerData getPlayer(UUID playerId) {
        return repository.findProjectionById(playerId)
                .map(PlayerDataProjection::toPlayerData)
                .orElse(null);
    }

    @Override
    public List<PlayerData> getPlayers(Set<UUID> playerIds) {
        return repository.findProjectionsByIds(playerIds)
                .stream()
                .map(PlayerDataProjection::toPlayerData)
                .toList();
    }
}
