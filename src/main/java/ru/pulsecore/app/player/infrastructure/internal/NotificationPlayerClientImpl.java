package ru.pulsecore.app.player.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPlayerClientImpl implements PlayerClient {

    private final PlayerCommandService  commandService;
    private final PlayerSearchService  searchService;

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
}
