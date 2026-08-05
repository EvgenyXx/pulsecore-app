package ru.pulsecore.app.player.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.infrastructure.config.SecurityUser;
import ru.pulsecore.app.player.api.AuthApi;
import ru.pulsecore.app.player.api.dto.response.AuthResponse;
import ru.pulsecore.app.player.api.dto.request.LoginRequest;
import ru.pulsecore.app.player.api.dto.response.MeResponse;
import ru.pulsecore.app.player.application.auth.AuthFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.util.Map;

@RestController
@RequestMapping(AuthApi.BASE_PATH)
@RequiredArgsConstructor
public class LoginController {

    private final AuthFacade authFacade;

    @PostMapping(AuthApi.LOGIN)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpSession session,
                                              HttpServletResponse response) {
        return ResponseEntity.ok(authFacade.login(request.getEmail(), request.getPassword(), session, response));
    }

    @GetMapping(AuthApi.ME)
    public ResponseEntity<MeResponse> me(HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof SecurityUser user)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authFacade.me(user.getPlayerId(), response));
    }

    @PostMapping(AuthApi.ME_THEME)
    public ResponseEntity<Void> setTheme(@CurrentPlayer PlayerPrincipal principal,
                                         @RequestBody Map<String, String> body) {
        authFacade.setTheme(principal.playerId().toString(), body.getOrDefault("theme", "dark"));
        return ResponseEntity.ok().build();
    }

    @PostMapping(AuthApi.LOGOUT)
    public ResponseEntity<Void> logout(HttpSession session, HttpServletResponse response) {
        authFacade.logout(session, response);
        return ResponseEntity.ok().build();
    }
}