package ru.pulsecore.app.modules.player.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.player.exception.SubscriptionRequiredException;
import ru.pulsecore.app.modules.player.service.subscription.SubscriptionService;
import ru.pulsecore.app.modules.shared.exception.UnauthorizedException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionInterceptor implements HandlerInterceptor {

    private final SubscriptionService subscriptionService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof SecurityUser user)) {
            throw new UnauthorizedException();
        }

        if (!subscriptionService.hasActiveSubscription(UUID.fromString(user.getPlayerId()))) {
            throw new SubscriptionRequiredException();//todo добавить во фронт
        }
        return true;
    }
}