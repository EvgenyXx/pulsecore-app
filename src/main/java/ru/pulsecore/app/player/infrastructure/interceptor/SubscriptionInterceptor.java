package ru.pulsecore.app.player.infrastructure.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.pulsecore.app.player.application.subscription.SubscriptionQueryService;
import ru.pulsecore.app.player.infrastructure.config.SecurityUser;
import ru.pulsecore.app.player.infrastructure.exception.SubscriptionRequiredException;
import ru.pulsecore.app.shared.exception.UnauthorizedException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionInterceptor implements HandlerInterceptor {

    private final SubscriptionQueryService subscriptionQueryServicenService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof SecurityUser user)) {
            throw new UnauthorizedException();
        }

        if (!subscriptionQueryServicenService.hasActiveSubscription(UUID.fromString(user.getPlayerId()))) {
            throw new SubscriptionRequiredException();//todo добавить во фронт
        }
        return true;
    }
}