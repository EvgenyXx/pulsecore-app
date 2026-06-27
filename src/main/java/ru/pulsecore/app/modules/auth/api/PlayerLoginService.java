// ==================== PlayerLoginService.java ====================
package ru.pulsecore.app.modules.auth.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.player.domain.Player;

import java.io.IOException;

@Service
public class PlayerLoginService {

    public void login(Player player, HttpServletRequest request) {
        SecurityUser securityUser = new SecurityUser(player);
        var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }

    public void loginAndRedirect(Player player, HttpServletRequest request, HttpServletResponse response) throws IOException {
        login(player, request);
        response.sendRedirect("/dashboard.html");
    }
}