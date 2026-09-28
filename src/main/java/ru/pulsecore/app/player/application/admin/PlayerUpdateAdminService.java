package ru.pulsecore.app.player.application.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.admin.api.dto.request.UpdatePlayerRequest;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.application.mapper.PlayerMapper;
import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerUpdateAdminService {

    private final PlayerCommandService  commandService;
    private final PlayerSearchService  searchService;
    private final PlayerMapper playerMapper;

    @Transactional
    public PlayerData updatePlayer(UUID playerId, UpdatePlayerRequest request) {
        log.info("Обновление игрока id={}", playerId);

        var player = searchService.getById(playerId);

        playerMapper.updateEntity(request, player);
        commandService.save(player);

        return playerMapper.toData(player);
    }
}