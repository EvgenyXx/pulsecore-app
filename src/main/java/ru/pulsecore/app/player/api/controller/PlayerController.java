package ru.pulsecore.app.player.api.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.player.api.dto.response.PlayerResponse;
import ru.pulsecore.app.player.application.player.PlayerFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PlayerController { //todo переименовать и сделать общую для всех юзер контроллеров

    private final PlayerFacade playerFacade;



    @GetMapping(PlayerApi.SEARCH)
    public ResponseEntity<List<PlayerResponse>> search(@RequestParam(PlayerApi.SEARCH_PARAM) String q) {
        return ResponseEntity.ok(playerFacade.searchPlayers(q));
    }



    @GetMapping(PlayerApi.HALLS)
    public ResponseEntity<Map<String, String>> getHalls(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(Map.of("halls", playerFacade.getSelectedHalls(principal.playerId())));
    }

    @PutMapping(PlayerApi.HALLS)
    public ResponseEntity<Void> saveHalls(@CurrentPlayer PlayerPrincipal principal,
                                          @RequestBody Map<String, String> body) {
        playerFacade.saveSelectedHalls(principal.playerId(), body.get("halls"));
        return ResponseEntity.ok().build();
    }
}