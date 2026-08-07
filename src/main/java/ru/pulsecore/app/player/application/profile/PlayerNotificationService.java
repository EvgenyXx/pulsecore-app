package ru.pulsecore.app.player.application.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.application.player.PlayerService;
import ru.pulsecore.app.player.domain.Player;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerNotificationService {

    private final PlayerService playerService;


    public boolean isNotificationsEnabled(UUID id) {
        return playerService.getById(id).isNotificationsEnabled();
    }


    public void setNotificationsEnabled(UUID id, boolean enabled) {
        Player player = playerService.getById(id);
        player.setNotificationsEnabled(enabled);
        playerService.save(player);
        log.info("🔔 Уведомления {} для игрока {} ({})", enabled ? "включены" : "отключены", player.getName(), id);
    }
}