package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.pulsecore.app.shared.dto.response.TournamentDto;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class DiscoveryHashUtil {

    public String calculateTournamentHash(TournamentDto t) {
        String players = t.getPlayers() != null
                ? t.getPlayers().stream().sorted().collect(Collectors.joining(","))
                : "";

        String content = t.getId() + "|" +
                players + "|" +
                (t.getDate() != null ? t.getDate().getDate() : "") + "|" +
                t.getHall();

        return Integer.toHexString(content.hashCode());
    }
}