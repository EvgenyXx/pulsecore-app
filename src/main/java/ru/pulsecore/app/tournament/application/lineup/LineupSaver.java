package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.repository.LineupRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LineupSaver {

    private final LineupRepository repository;

    public void saveAll(List<Lineup> lineups) {
        for (Lineup lineup : lineups) {
            repository.upsertLineup(
                    lineup.getDate(),
                    lineup.getLeague(),
                    lineup.getTime(),
                    lineup.getHall(),
                    lineup.getPlayers(),
                    lineup.getStreamUrl(),
                    lineup.getLink(),
                    lineup.getType() != null ? lineup.getType().name() : null
            );
        }
    }
}