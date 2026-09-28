package ru.pulsecore.app.player.api.dto.response;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PlayerProfileResponse {
    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}