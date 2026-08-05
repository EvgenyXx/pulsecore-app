package ru.pulsecore.app.tournament.application.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import ru.pulsecore.app.shared.dto.TournamentDto;
import ru.pulsecore.app.shared.security.PlayerPrincipal;
import ru.pulsecore.app.shared.security.PlayerPrincipalExtractor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentFacade {

    private final PlayerPrincipalExtractor extractor;
    private final TournamentSearchService tournamentSearchService;
    private final TournamentResultService tournamentResultService;



    public List<TournamentDto> searchTournaments(String date, String endDate) {
        PlayerPrincipal principal = extractor.extract();
        String playerName = principal.name();
        if (endDate != null && !endDate.isEmpty()) {
            return tournamentSearchService.findByDateRangeAndPlayer(date, endDate, playerName);
        }
        return tournamentSearchService.findByDateAndPlayer(date, playerName);
    }



    public void updateResult(Long tournamentId, Double amount, Double bonus) {
        extractor.extract();
        tournamentResultService.updateResult(tournamentId, amount, bonus);
    }


}