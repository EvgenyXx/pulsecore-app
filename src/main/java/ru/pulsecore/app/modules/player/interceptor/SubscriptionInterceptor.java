package ru.pulsecore.app.modules.player.interceptor;

import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.player.service.subscribion.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionInterceptor implements HandlerInterceptor {

    private final SubscriptionService subscriptionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof SecurityUser user)) {
            response.setStatus(401);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"message\":\"Не авторизован\"}");
            return false;
        }

        if (!subscriptionService.hasActiveSubscription(UUID.fromString(user.getPlayerId()))) {
            response.setStatus(402);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"message\":\"Требуется активная подписка\"}");
            return false;
        }
        return true;
    }
}