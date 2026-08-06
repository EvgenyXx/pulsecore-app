package ru.pulsecore.app.player.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.pulsecore.app.player.infrastructure.config.SecurityUser;
import ru.pulsecore.app.player.infrastructure.persistence.entity.Player;
import ru.pulsecore.app.player.application.player.PlayerService;
import ru.pulsecore.app.player.infrastructure.properties.SecurityProperties;
import ru.pulsecore.app.player.infrastructure.session.RememberMeService;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionRestoreFilter extends OncePerRequestFilter {

    private final RememberMeService rememberMeService;
    private final PlayerService playerService;
    private final SecurityProperties securityProperties;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            tryRestore(request, response);
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return securityProperties.getSessionRestoreExcludeUrls().stream()
                .anyMatch(path::startsWith);
    }

    private void tryRestore(HttpServletRequest request, HttpServletResponse response) {
        rememberMeService.getPlayerId(request)
                .flatMap(id -> playerService.findByIdOptional(UUID.fromString(id)))
                .ifPresent(player -> restoreSession(request, response, player));
    }

    private void restoreSession(HttpServletRequest request, HttpServletResponse response, Player player) {
        HttpSession session = request.getSession(true);
        SecurityUser securityUser = new SecurityUser(player);
        var authToken = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
        rememberMeService.refresh(response, player.getId().toString());
    }
}