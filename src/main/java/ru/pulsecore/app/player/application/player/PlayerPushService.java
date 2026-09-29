package ru.pulsecore.app.player.application.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.domain.Player;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerPushService {

    private final PlayerSearchService playerSearchService;
    private final PlayerCommandService playerCommandService;

    @Transactional
    public boolean togglePushEnabled(UUID playerId) {
        Player player = playerSearchService.getById(playerId);
        player.setPushEnabled(!player.isPushEnabled());
        playerCommandService.save(player);
        log.info("Push-уведомления {} для игрока {} ({})",
                player.isPushEnabled() ? "включены" : "отключены",
                player.getName(), playerId);
        return player.isPushEnabled();
    }
}