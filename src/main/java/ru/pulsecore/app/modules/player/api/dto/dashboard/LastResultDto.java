package ru.pulsecore.app.modules.player.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastResultDto implements Serializable {
    private String date;
    private Double amount;
    private String tournamentLink;
}