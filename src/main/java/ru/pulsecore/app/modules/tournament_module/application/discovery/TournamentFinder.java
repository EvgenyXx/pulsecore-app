package ru.pulsecore.app.modules.tournament_module.application.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.tournament_module.application.tournament.UpcomingTournamentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentFinder {

    private final UpcomingTournamentService tournamentService;

    public List<TournamentDto> find(String name) {
        return tournamentService.findPlayerTournaments(name);
    }
}