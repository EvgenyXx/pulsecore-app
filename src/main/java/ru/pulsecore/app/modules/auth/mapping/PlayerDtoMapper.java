package ru.pulsecore.app.modules.auth.mapping;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.auth.api.dto.AuthResponse;
import ru.pulsecore.app.modules.player.domain.Player;

@Component
public class PlayerDtoMapper {
    public AuthResponse toAuthResponse(Player player) {
        return AuthResponse.builder()
                .id(player.getId().toString())
                .name(player.getName())
                .email(player.getEmail())
                .build();
    }
}