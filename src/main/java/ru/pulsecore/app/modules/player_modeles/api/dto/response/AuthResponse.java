package ru.pulsecore.app.modules.player_modeles.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String id;
    private String name;
    private String email;
}