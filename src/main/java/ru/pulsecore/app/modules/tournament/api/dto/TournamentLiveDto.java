package ru.pulsecore.app.modules.tournament.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TournamentLiveDto {
    private Long externalId;
    private String league;
    private String hall;
    private String time;
    private String date;
    private List<String> players;
    private String streamUrl;
}