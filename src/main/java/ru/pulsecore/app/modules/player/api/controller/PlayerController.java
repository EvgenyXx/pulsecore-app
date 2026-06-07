package ru.pulsecore.app.modules.player.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.auth.api.dto.ChangePasswordRequest;
import ru.pulsecore.app.modules.auth.api.dto.UpdateProfileRequest;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.MessageResponse;
import ru.pulsecore.app.modules.player.api.dto.NotificationsStatusResponse;
import ru.pulsecore.app.modules.player.api.dto.PlayerProfileResponse;
import ru.pulsecore.app.modules.player.api.dto.PlayerResponse;

import ru.pulsecore.app.modules.player.service.player.PlayerFacade;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerFacade playerFacade;

    @PutMapping(PlayerApi.PROFILE)
    public ResponseEntity<PlayerProfileResponse> updateProfile(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(playerFacade.updateProfile(principal.playerId(), request));
    }

    @PutMapping(PlayerApi.CHANGE_PASSWORD)
    public ResponseEntity<MessageResponse> changePassword(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(playerFacade.changePassword(principal.playerId(), request));
    }

    @GetMapping(PlayerApi.SEARCH)
    public ResponseEntity<List<PlayerResponse>> search(@RequestParam(PlayerApi.SEARCH_PARAM) String q) {
        return ResponseEntity.ok(playerFacade.searchPlayers(q));
    }


    @PutMapping(PlayerApi.NOTIFICATIONS)
    public ResponseEntity<MessageResponse> toggleNotifications(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok(playerFacade.toggleNotifications(principal.playerId(), enabled));
    }

    @GetMapping(PlayerApi.NOTIFICATIONS_STATUS)
    public ResponseEntity<NotificationsStatusResponse> getNotificationsStatus(
            @CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(playerFacade.getNotificationsStatus(principal.playerId()));
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