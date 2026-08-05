package ru.pulsecore.app.tournament.application.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.TournamentDto;
import ru.pulsecore.app.tournament.application.tournament.UpcomingTournamentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentFinder {

    private final UpcomingTournamentService tournamentService;

    public List<TournamentDto> find(String name) {
        return tournamentService.findPlayerTournaments(name);
    }
}