package ru.pulsecore.app.modules.player.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String id;
    private String name;
    private String email;
}