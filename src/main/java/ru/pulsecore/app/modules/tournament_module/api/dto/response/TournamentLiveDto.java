package ru.pulsecore.app.modules.tournament_module.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pulsecore.app.modules.tournament_module.domain.LiveStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentLiveDto {
    private Long externalId;
    private String league;
    private String hall;
    private String time;
    private String date;
    private List<String> players;
    private String streamUrl;
    private LiveStatus status;

}