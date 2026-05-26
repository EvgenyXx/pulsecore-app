package ru.pulsecore.app.modules.shared.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String user;

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof PlayerPrincipal principal) {
            user = principal.name(); // или principal.getPlayer().getName()
        } else {
            user = "anonymous";
        }

        String uri = request.getRequestURI();
        if (uri.startsWith("/api/") || uri.endsWith(".html")) {
            log.info("📊 {} -> {} {}", user, request.getMethod(), uri);
        }

        chain.doFilter(request, response);
    }
}