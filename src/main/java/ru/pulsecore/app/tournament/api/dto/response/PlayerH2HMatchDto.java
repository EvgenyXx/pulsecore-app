package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlayerH2HMatchDto {

    private LocalDateTime playedAt;
    private String score;
    private String winnerName;
}