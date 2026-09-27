package ru.pulsecore.app.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.infrastructure.config.SecurityUser;

import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerPrincipalExtractor {

    public Optional<PlayerPrincipal> extract() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser user)) {
            return Optional.empty();
        }

        return Optional.of(new PlayerPrincipal(
                UUID.fromString(user.getPlayerId()),
                user.getEmail(),
                user.getPlayerName()
        ));
    }
}