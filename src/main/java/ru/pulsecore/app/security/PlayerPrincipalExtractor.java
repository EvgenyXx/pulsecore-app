package ru.pulsecore.app.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.config.SecurityUser;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerPrincipalExtractor {

    public PlayerPrincipal extract() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = (SecurityUser) auth.getPrincipal();

        return new PlayerPrincipal(
                UUID.fromString(user.getPlayerId()),
                user.getEmail(),
                user.getPlayerName()
        );
    }
}