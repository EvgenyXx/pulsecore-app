// modules/shared/config/RequestLoggingFilter.java
package ru.pulsecore.app.modules.shared.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.shared.service.PageViewService;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final PageViewService pageViewService;
    private final PlayerRepository playerRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/")) {
            String email = "anonymous";
            UUID playerId = null;

            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                email = auth.getName();
                Optional<Player> player = playerRepository.findByEmail(email);
                if (player.isPresent()) {
                    playerId = player.get().getId();
                }
                log.debug("📊 {} -> {} {}", email, request.getMethod(), uri);
            } else {
                log.warn("📊 ANON -> {} {}", request.getMethod(), uri);
            }

            pageViewService.save(playerId, email, uri, request.getMethod(),
                    request.getHeader("User-Agent"), request.getRemoteAddr());
        }

        chain.doFilter(request, response);
    }
}