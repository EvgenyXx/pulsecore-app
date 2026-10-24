package ru.pulsecore.app.player.application.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.repository.PlayerRepository;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerPushService {

    private final PlayerSearchService playerSearchService;
    private final PlayerRepository playerRepository;
    @Transactional
    public boolean togglePushEnabled(UUID playerId) {
        Player player = playerSearchService.getById(playerId);
        player.setPushEnabled(!player.isPushEnabled());
        playerRepository.save(player);
        log.info("Push-уведомления {} для игрока {} ({})",
                player.isPushEnabled() ? "включены" : "отключены",
                player.getName(), playerId);
        return player.isPushEnabled();
    }
}