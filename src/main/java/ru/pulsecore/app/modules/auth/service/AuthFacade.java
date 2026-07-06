package ru.pulsecore.app.modules.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.auth.api.dto.AuthResponse;
import ru.pulsecore.app.modules.auth.api.dto.MeResponse;
import ru.pulsecore.app.modules.auth.mapping.PlayerDtoMapper;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.shared.service.session.RememberMeService;
import ru.pulsecore.app.modules.shared.service.theme.ThemeService;
import ru.pulsecore.app.modules.shared.service.auth.PlayerAuthenticationService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final PlayerAuthenticationService authenticationService;
    private final PlayerService playerService;
    private final PlayerDtoMapper mapper;
    private final ThemeService themeService;
    private final RememberMeService rememberMeService;

    public AuthResponse login(String email, String rawPassword, HttpSession session, HttpServletResponse response) {
        Player player = authenticationService.authenticate(email, rawPassword);
        AuthResponse authResponse = mapper.toAuthResponse(player);

        SecurityUser securityUser = new SecurityUser(player);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        rememberMeService.set(response, player.getId().toString());
        return authResponse;
    }

    public MeResponse me(String playerId, HttpServletResponse response) {
        rememberMeService.refresh(response, playerId);

        var player = playerService.findById(UUID.fromString(playerId));
        String theme = themeService.getTheme(UUID.fromString(playerId));

        return new MeResponse(
                playerId,
                player != null ? player.getName() : null,
                player != null ? player.getEmail() : null,
                player != null ? player.getCreatedAt() : null,
                player != null && player.isAdmin(),
                theme
        );
    }

    public void setTheme(String playerId, String theme) {
        themeService.setTheme(UUID.fromString(playerId), theme);
    }

    public void logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        rememberMeService.clear(response);
    }
}