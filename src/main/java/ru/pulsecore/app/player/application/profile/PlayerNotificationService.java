package ru.pulsecore.app.player.application.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.repository.PlayerRepository;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerNotificationService {

    private final PlayerSearchService playerSearchService;
    private final PlayerRepository playerRepository;


    public boolean isNotificationsEnabled(UUID id) {
        return playerSearchService.getById(id).isNotificationsEnabled();
    }
    @Transactional
    public void setNotificationsEnabled(UUID id, boolean enabled) {
        Player player = playerSearchService.getById(id);
        player.setNotificationsEnabled(enabled);
        playerRepository.save(player);
        log.info("🔔 Уведомления {} для игрока {} ({})", enabled ? "включены" : "отключены", player.getName(), id);
    }
}