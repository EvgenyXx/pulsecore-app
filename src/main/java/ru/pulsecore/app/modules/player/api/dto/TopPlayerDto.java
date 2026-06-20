package ru.pulsecore.app.modules.player.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TopPlayerDto implements Serializable {
    private String name;
    private Long tournaments;
}