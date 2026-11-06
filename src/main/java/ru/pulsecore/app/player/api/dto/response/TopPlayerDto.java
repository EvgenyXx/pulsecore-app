package ru.pulsecore.app.player.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopPlayerDto implements Serializable {

    private UUID playerId;
    private String name;
    private DetailPeriod detailPeriod;
    private String primaryLeague;
}