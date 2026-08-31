package ru.pulsecore.app.player.application.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.infrastructure.config.SecurityUser;
import ru.pulsecore.app.player.api.dto.response.AuthResponse;
import ru.pulsecore.app.player.api.dto.response.MeResponse;
import ru.pulsecore.app.player.infrastructure.persistence.mapping.PlayerDtoMapper;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.session.RememberMeService;
import ru.pulsecore.app.player.application.profile.ThemeService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final PlayerAuthenticationService authenticationService;
    private final PlayerSearchService playerSearchService;
    private final PlayerDtoMapper mapper;
    private final ThemeService themeService;
    private final RememberMeService rememberMeService;
    private final PlayerCommandService playerCommandService;

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

    @Transactional
    public MeResponse me(String playerId, HttpServletResponse response) {
        rememberMeService.refresh(response, playerId);

        Player player = playerSearchService.getById(UUID.fromString(playerId));
        player.setLastLoginAt(LocalDateTime.now());
        playerCommandService.save(player);

        String theme = themeService.getTheme(UUID.fromString(playerId));

        return new MeResponse(
                playerId,
                player.getName(),
                player.getEmail(),
                player.getCreatedAt(),
                player.isAdmin(),
                theme
        );
    }

    public void logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        rememberMeService.clear(response);
    }
}