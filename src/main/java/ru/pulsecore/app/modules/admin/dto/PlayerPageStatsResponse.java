// modules/admin/dto/PlayerPageStatsResponse.java
package ru.pulsecore.app.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPageStatsResponse {
    private String name;
    private String paths;      // "📊 Дашборд=5|📋 Мои турниры=3|..."
    private Long total;
    private Double percent;
}