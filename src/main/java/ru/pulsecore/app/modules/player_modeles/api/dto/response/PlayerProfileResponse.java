package ru.pulsecore.app.modules.player_modeles.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlayerProfileResponse {
    private String id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}