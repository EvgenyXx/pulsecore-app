package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.api.PlayerApi;
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

@Tag(name = "Player", description = "Профиль игрока: настройки, безопасность, тема")
@RequestMapping(PlayerApi.BASE_PATH)
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileFacade profileFacade;

    @Operation(summary = "Подтвердить текущий пароль")
    @PostMapping(PlayerApi.VERIFY_PASSWORD)
    public ResponseEntity<MessageResponse> verifyPassword(
            @Valid @RequestBody VerifyPasswordRequest request, @CurrentPlayer PlayerPrincipal principal) {
        profileFacade.verifyPassword(principal.playerId(), request.getPassword());
        return ResponseEntity.ok(new MessageResponse(PlayerApi.OK));
    }

    @Operation(summary = "Обновить email в профиле")
    @PutMapping(PlayerApi.UPDATE_PLAYER)
    public ResponseEntity<PlayerProfileResponse> updateProfile(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileFacade.updateProfile(principal.playerId(), request.getEmail()));
    }

    @Operation(summary = "Сменить пароль")
    @PutMapping(PlayerApi.CHANGE_PASSWORD)
    public ResponseEntity<MessageResponse> changePassword(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(profileFacade.changePassword(
                principal.playerId(),
                request.getOldPassword(),
                request.getNewPassword()));
    }

    @Operation(summary = "Включить или выключить уведомления")
    @PutMapping(PlayerApi.NOTIFICATIONS)
    public ResponseEntity<MessageResponse> toggleNotifications(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok(profileFacade.toggleNotifications(principal.playerId(), enabled));
    }

    @Operation(summary = "Получить статус уведомлений")
    @GetMapping(PlayerApi.NOTIFICATIONS_STATUS)
    public ResponseEntity<NotificationsStatusResponse> getNotificationsStatus(
            @CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(profileFacade.getNotificationsStatus(principal.playerId()));
    }

    @Operation(summary = "Сохранить тему оформления")
    @PostMapping(PlayerApi.ME_THEME)//todo сделать дто request
    public ResponseEntity<Void> setTheme(@CurrentPlayer PlayerPrincipal principal,
                                         @RequestBody Map<String, String> body) {
        profileFacade.setTheme(principal.playerId().toString(),
                body.getOrDefault("theme", "dark"));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить QR-код для авторизации")
    @GetMapping(PlayerApi.QR)
    public ResponseEntity<byte[]> getQrCode() throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(profileFacade.generateQrCode());
    }
}