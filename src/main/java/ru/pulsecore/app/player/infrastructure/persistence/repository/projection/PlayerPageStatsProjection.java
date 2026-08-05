package ru.pulsecore.app.player.infrastructure.persistence.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPageStatsProjection {
    private String name;
    private String paths;     
    private Long total;
    private Double percent;
}