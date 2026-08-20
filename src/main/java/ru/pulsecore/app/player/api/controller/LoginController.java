package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.player.infrastructure.config.SecurityUser;
import ru.pulsecore.app.player.api.dto.response.AuthResponse;
import ru.pulsecore.app.player.api.dto.request.LoginRequest;
import ru.pulsecore.app.player.api.dto.response.MeResponse;
import ru.pulsecore.app.player.application.auth.AuthFacade;

@Tag(name = "Auth", description = "Вход, выход, текущий пользователь")
@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class LoginController {

    private final AuthFacade authFacade;

    @Operation(summary = "Войти в систему")
    @PostMapping(PlayerApi.LOGIN)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpSession session,
                                              HttpServletResponse response) {
        return ResponseEntity.ok(authFacade.login(request.getEmail(), request.getPassword(), session, response));
    }

    @Operation(summary = "Получить данные текущего пользователя")
    @GetMapping(PlayerApi.ME)
    public ResponseEntity<MeResponse> me(HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof SecurityUser user)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authFacade.me(user.getPlayerId(), response));
    }

    @Operation(summary = "Выйти из системы")
    @PostMapping(PlayerApi.LOGOUT)
    public ResponseEntity<Void> logout(HttpSession session, HttpServletResponse response) {
        authFacade.logout(session, response);
        return ResponseEntity.ok().build();
    }
}