package ru.pulsecore.app.modules.player.service.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerNotificationService {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    public boolean isNotificationsEnabled(UUID id) {
        return playerService.getById(id).isNotificationsEnabled();
    }

    @Transactional
    public void setNotificationsEnabled(UUID id, boolean enabled) {
        Player player = playerService.getById(id);
        player.setNotificationsEnabled(enabled);
        playerRepository.save(player);
        log.info("🔔 Уведомления {} для игрока {} ({})", enabled ? "включены" : "отключены", player.getName(), id);
    }
}