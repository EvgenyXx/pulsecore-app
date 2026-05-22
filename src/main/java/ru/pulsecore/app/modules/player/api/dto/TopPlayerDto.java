package ru.pulsecore.app.modules.player.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopPlayerDto {
    private String name;
    private Long tournaments;
}