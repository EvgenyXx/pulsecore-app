package ru.pulsecore.app.player.application.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.domain.Player;
import java.util.UUID;


/**
 * Управление залами игрока для расписания и лайв-трансляций.
 * Сохраняет и возвращает выбранные залы.
 */
@Service
@RequiredArgsConstructor
public class PlayerHallsService {

    private final PlayerCommandService playerCommandService;
    private final PlayerSearchService playerSearchService;



    @Transactional
    public void saveSelectedHalls(UUID playerId, String halls) {
        Player player = playerSearchService.getById(playerId);
        player.setSelectedHalls(halls);
        playerCommandService.save(player);
    }



    @Transactional
    public void saveLiveSelectedHalls(UUID playerId, String halls) {
        Player player = playerSearchService.getById(playerId);
        player.setLiveSelectedHalls(halls);
        playerCommandService.save(player);
    }


    public String getSelectedHalls(UUID playerId) {
        Player player = playerSearchService.getById(playerId);
        return player.getSelectedHalls();
    }

    public String getLiveSelectedHalls(UUID playerId) {
       Player player = playerSearchService.getById(playerId);
       return player.getLiveSelectedHalls();
    }
}