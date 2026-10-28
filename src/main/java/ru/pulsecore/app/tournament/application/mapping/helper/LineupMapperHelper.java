package ru.pulsecore.app.tournament.application.mapping.helper;

import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerIdentity;

import java.util.Arrays;

@Component
public class LineupMapperHelper {

    @Named(MappingConstants.IS_PLAYER_IN_LINEUP)
    public boolean isPlayerInLineup(String playersCsv, @Context String playerName) {
        if (playersCsv == null || playerName == null) return false;

        return Arrays.stream(playersCsv.split(","))
                .anyMatch(p -> PlayerIdentity.isSamePlayer(p, playerName));
    }
}