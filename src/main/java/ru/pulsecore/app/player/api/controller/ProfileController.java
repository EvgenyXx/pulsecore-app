package ru.pulsecore.app.player.api.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.api.AuthApi;
import ru.pulsecore.app.player.api.ProfileApi;
import ru.pulsecore.app.player.api.anatation.ProfileControllerAn;
import ru.pulsecore.app.player.api.dto.request.ChangePasswordRequest;
import ru.pulsecore.app.player.api.dto.request.UpdateProfileRequest;
import ru.pulsecore.app.player.api.dto.request.VerifyPasswordRequest;
import ru.pulsecore.app.player.api.dto.response.NotificationsStatusResponse;
import ru.pulsecore.app.player.api.dto.response.PlayerProfileResponse;
import ru.pulsecore.app.player.application.profile.ProfileFacade;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.util.Map;


@ProfileControllerAn
@RequiredArgsConstructor
public class ProfileController {


    private final ProfileFacade profileFacade;


    @PostMapping(ProfileApi.VERIFY_PASSWORD)
    public ResponseEntity<MessageResponse> verifyPassword(
            @Valid @RequestBody VerifyPasswordRequest request,@CurrentPlayer PlayerPrincipal principal) {

        profileFacade.verifyPassword(principal.playerId(), request.getPassword());
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }



    @PutMapping(ProfileApi.UPDATE_PLAYER)
    public ResponseEntity<PlayerProfileResponse> updateProfile(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileFacade.updateProfile(principal.playerId(), request.getEmail()));
    }

    @PutMapping(ProfileApi.CHANGE_PASSWORD)
    public ResponseEntity<MessageResponse> changePassword(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(profileFacade.changePassword(principal.playerId(), request));
    }



    @PutMapping(ProfileApi.NOTIFICATIONS)
    public ResponseEntity<MessageResponse> toggleNotifications(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok(profileFacade.toggleNotifications(principal.playerId(), enabled));
    }

    @GetMapping(ProfileApi.NOTIFICATIONS_STATUS)
    public ResponseEntity<NotificationsStatusResponse> getNotificationsStatus(
            @CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(profileFacade.getNotificationsStatus(principal.playerId()));
    }


    @PostMapping(ProfileApi.ME_THEME)//todo сделать дто request
    public ResponseEntity<Void> setTheme(@CurrentPlayer PlayerPrincipal principal,
                                         @RequestBody Map<String, String> body) {
        profileFacade.setTheme(principal.playerId().toString(),
                body.getOrDefault("theme", "dark"));
        return ResponseEntity.ok().build();
    }

    @GetMapping(ProfileApi.QR)
    public ResponseEntity<byte[]> getQrCode() throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(profileFacade.generateQrCode());
    }
}
