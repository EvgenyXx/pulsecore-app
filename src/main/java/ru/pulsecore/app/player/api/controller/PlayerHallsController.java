package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.player.application.player.PlayerHallsService;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;
import java.util.Map;

@Tag(name = "Player", description = "Залы игрока для расписания и лайв-трансляций")
@RestController
@RequiredArgsConstructor
@RequestMapping(PlayerApi.BASE_PATH)
public class PlayerHallsController {

    private final PlayerHallsService playerHallsService;

    @Operation(summary = "Сохранить выбранные залы для лайв-трансляций")
    @PostMapping(PlayerApi.SAVE_LIVE_HALLS)
    public ResponseEntity<Void> saveLiveHalls(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestBody(required = false) String halls) {
        playerHallsService.saveLiveSelectedHalls(principal.playerId(), halls);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Сохранить выбранные залы для расписания турниров")
    @PutMapping(PlayerApi.SAVE_HALLS)
    public ResponseEntity<Void> saveHalls(@CurrentPlayer PlayerPrincipal principal,
                                          @RequestBody Map<String, String> body) {
        playerHallsService.saveSelectedHalls(principal.playerId(), body.get("halls"));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить сохраненные залы для расписания турниров")
    @GetMapping(PlayerApi.GET_PLAYER_HALLS)
    public ResponseEntity<Map<String, String>> getHalls(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(Map.of("halls", playerHallsService.getSelectedHalls(principal.playerId())));
    }

    @Operation(summary = "Получить сохраненные залы для лайв-трансляций")
    @GetMapping(PlayerApi.GET_LIVE_HALLS)
    public ResponseEntity<String> getLiveSelectedHalls(@CurrentPlayer PlayerPrincipal currentPlayer) {
        return ResponseEntity.ok(playerHallsService.getLiveSelectedHalls(currentPlayer.playerId()));
    }

}