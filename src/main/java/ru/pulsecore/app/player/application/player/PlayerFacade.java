package ru.pulsecore.app.player.application.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.PlayerResponse;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerFacade {



    private final PlayerHallsService hallsService;
    private final PlayerService playerService;



    public List<PlayerResponse> searchPlayers(String query) {
        return playerService.searchPlayers(query);
    }


    public void saveSelectedHalls(UUID playerId, String halls) {
        hallsService.saveSelectedHalls(playerId, halls);
    }

    public String getSelectedHalls(UUID playerId) {
        return hallsService.getSelectedHalls(playerId);
    }
}