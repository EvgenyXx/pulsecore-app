package ru.pulsecore.app.modules.player.service.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerHallsService {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    public String getSelectedHalls(UUID playerId) {
        return playerService.getById(playerId).getSelectedHalls();
    }

    @Transactional
    public void saveSelectedHalls(UUID playerId, String halls) {
        Player player = playerService.getById(playerId);
        player.setSelectedHalls(halls);
        playerRepository.save(player);
    }

    public String getLiveSelectedHalls(UUID playerId) {
        return playerService.getById(playerId).getLiveSelectedHalls();
    }

    @Transactional
    public void saveLiveSelectedHalls(UUID playerId, String halls) {
        Player player = playerService.getById(playerId);
        player.setLiveSelectedHalls(halls);
        playerRepository.save(player);
    }
}