package ru.pulsecore.app.modules.player.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse implements Serializable {
    private String playerName;
    private LastResultDto lastResult;
    private List<UpcomingLineupDto> upcomingLineups;
    private SubscriptionInfoDto subscription;

    private String primaryLeague;
}