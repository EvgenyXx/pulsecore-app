package ru.pulsecore.app.tournament.application.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.enums.TournamentVersion;
import ru.pulsecore.app.tournament.infrastructure.exception.StageParserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageParserResolver {


    private final List<StageParser> parsers;

    public StageParser resolve(String typeId) {
        TournamentVersion version = TournamentVersion.fromTypeId(typeId)
                .orElse(TournamentVersion.FOUR_PL_NEW);
        return parsers.stream()
                .filter(p -> p.supports(version))
                .findFirst()
                .orElseThrow(StageParserNotFoundException::new);
    }
}