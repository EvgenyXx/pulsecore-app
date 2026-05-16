package ru.pulsecore.app.modules.player.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.auth.api.dto.ChangePasswordRequest;
import ru.pulsecore.app.modules.auth.api.dto.UpdateProfileRequest;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.*;
import ru.pulsecore.app.modules.player.service.PlayerFacade;
import ru.pulsecore.app.modules.shared.security.CurrentPlayer;
import ru.pulsecore.app.modules.shared.security.PlayerPrincipal;

import java.util.List;

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

    @DeleteMapping(PlayerApi.DELETE_ACCOUNT)
    public ResponseEntity<MessageResponse> deleteAccount(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(playerFacade.deleteAccount(principal.playerId()));
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
}