package ru.pulsecore.app.modules.auth.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.auth.api.dto.AuthResponse;
import ru.pulsecore.app.modules.auth.api.dto.LoginRequest;
import ru.pulsecore.app.modules.auth.api.dto.MeResponse;
import ru.pulsecore.app.modules.auth.mapping.PlayerDtoMapper;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.shared.properties.SessionProperties;
import ru.pulsecore.app.modules.shared.service.ThemeService;
import ru.pulsecore.app.modules.shared.service.auth.PlayerAuthenticationService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(AuthApi.BASE_PATH)
@RequiredArgsConstructor
public class LoginController {

    private final PlayerAuthenticationService authenticationService;
    private final PlayerService playerService;
    private final PlayerDtoMapper mapper;
    private final SessionProperties sessionProperties;
    private final ThemeService themeService;

    @PostMapping(AuthApi.LOGIN)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpSession session) {
        var player = authenticationService.authenticate(request.getEmail(), request.getPassword());
        var response = mapper.toAuthResponse(player);

        SecurityUser securityUser = new SecurityUser(player);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(response);
    }

    @GetMapping(AuthApi.ME)
    public ResponseEntity<MeResponse> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof SecurityUser user)) {
            return ResponseEntity.status(401).build();
        }

        var player = playerService.findById(UUID.fromString(user.getPlayerId()));
        String theme = themeService.getTheme(UUID.fromString(user.getPlayerId()));

        return ResponseEntity.ok(new MeResponse(
                user.getPlayerId(),
                user.getPlayerName(),
                user.getEmail(),
                player != null ? player.getCreatedAt() : null,
                player != null && player.isAdmin(),
                theme
        ));
    }

    @PostMapping("/me/theme")
    public ResponseEntity<Void> setTheme(@RequestBody Map<String, String> body) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof SecurityUser user)) {
            return ResponseEntity.status(401).build();
        }
        String theme = body.getOrDefault("theme", "dark");
        themeService.setTheme(UUID.fromString(user.getPlayerId()), theme);
        return ResponseEntity.ok().build();
    }

    @PostMapping(AuthApi.LOGOUT)
    public ResponseEntity<Void> logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie(sessionProperties.getName(), null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(sessionProperties.isSecure());
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}