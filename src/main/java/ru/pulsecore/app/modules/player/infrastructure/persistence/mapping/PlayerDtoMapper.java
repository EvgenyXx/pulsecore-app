package ru.pulsecore.app.modules.player.infrastructure.persistence.mapping;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.api.dto.response.AuthResponse;
import ru.pulsecore.app.modules.player.entity.Player;

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