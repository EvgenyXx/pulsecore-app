package ru.pulsecore.app.modules.player.api.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.auth.api.dto.ChangePasswordRequest;
import ru.pulsecore.app.modules.auth.api.dto.UpdateProfileRequest;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.*;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.shared.propirties.SessionProperties;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final SessionProperties sessionProperties;// 6

    @PutMapping(PlayerApi.PROFILE)
    public ResponseEntity<PlayerProfileResponse> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(playerService.updateProfile(id, request));
    }

    @PutMapping(PlayerApi.CHANGE_PASSWORD)
    public ResponseEntity<MessageResponse> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        playerService.changePassword(id, request);
        return ResponseEntity.ok(new MessageResponse("Пароль изменён"));
    }

    @GetMapping(PlayerApi.SEARCH)
    public ResponseEntity<List<PlayerResponse>> search(@RequestParam(PlayerApi.SEARCH_PARAM) String q) {
        return ResponseEntity.ok(playerService.searchPlayers(q));
    }

    @DeleteMapping(PlayerApi.DELETE_ACCOUNT)
    public ResponseEntity<MessageResponse> deleteAccount(@PathVariable UUID id,
                                                         HttpSession session,
                                                         HttpServletResponse response) {
        playerService.deletePlayer(id);
        session.invalidate();
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie(sessionProperties.getName(), null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(new MessageResponse("Аккаунт удалён"));
    }

    @PutMapping(PlayerApi.NOTIFICATIONS)
    public ResponseEntity<MessageResponse> toggleNotifications(
            @PathVariable UUID id, @RequestParam boolean enabled) {
        playerService.setNotificationsEnabled(id, enabled);
        return ResponseEntity.ok(new MessageResponse(enabled ? "Уведомления включены" : "Уведомления отключены"));
    }

    @GetMapping(PlayerApi.NOTIFICATIONS_STATUS)
    public ResponseEntity<NotificationsStatusResponse> getNotificationsStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(new NotificationsStatusResponse(playerService.isNotificationsEnabled(id)));
    }
}