package ru.pulsecore.app.tournament.infrastructure.util.hash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.TournamentDto;

import java.util.stream.Collectors;


@Profile("hash-legacy")
@Slf4j
@Component
public class DiscoveryHashUtil implements TournamentHashCalculator {

    @Override
    public String calculateTournamentHash(TournamentDto tournament) {
        String players = tournament.getPlayers() != null
                ? tournament.getPlayers().stream().sorted().collect(Collectors.joining(","))
                : "";

        String content = tournament.getId() + "|" +
                players + "|" +
                (tournament.getDate() != null ? tournament.getDate().getDate() : "") + "|" +
                tournament.getHall();

        String hash = Integer.toHexString(content.hashCode());

        log.debug("Вычислен хеш турнира: tournamentId={}, algorithm=legacy, hash={}",
                tournament.getId(), hash);

        return hash;
    }
}