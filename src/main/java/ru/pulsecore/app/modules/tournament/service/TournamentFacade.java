package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.notification.service.TournamentProcessService;
import ru.pulsecore.app.modules.tournament.api.dto.AddTournamentRequest;
import ru.pulsecore.app.modules.tournament.api.dto.AddTournamentResponse;
import ru.pulsecore.app.modules.tournament.api.dto.TournamentSearchResult;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.security.PlayerPrincipal;
import ru.pulsecore.app.security.PlayerPrincipalExtractor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentFacade {

    private final PlayerPrincipalExtractor extractor;
    private final TournamentProcessService tournamentProcessService;
    private final TournamentSearchService tournamentSearchService;
    private final TournamentResultService tournamentResultService;

    public AddTournamentResponse addByUrl(AddTournamentRequest request) {
        PlayerPrincipal principal = extractor.extract();
        return tournamentProcessService.processByUrl(request.getUrl(), principal.playerId().toString());
    }

    public List<TournamentDto> searchTournaments(String date, String endDate) {
        PlayerPrincipal principal = extractor.extract();
        String playerName = principal.name();
        if (endDate != null && !endDate.isEmpty()) {
            return tournamentSearchService.findByDateRangeAndPlayer(date, endDate, playerName);
        }
        return tournamentSearchService.findByDateAndPlayer(date, playerName);
    }

    public List<AddTournamentResponse> addByUrls(List<AddTournamentRequest> requests) {
        PlayerPrincipal principal = extractor.extract();
        List<String> urls = requests.stream().map(AddTournamentRequest::getUrl).toList();
        return tournamentProcessService.processByUrls(urls, principal.playerId().toString());
    }

    public void updateResult(Long tournamentId, Double amount, Double bonus) {
        extractor.extract(); // просто проверяем авторизацию, кидаем 401 если нет
        tournamentResultService.updateResult(tournamentId, amount, bonus);
    }

    public List<TournamentSearchResult> searchTournamentsWithStatus(String date, String endDate) {
        PlayerPrincipal principal = extractor.extract();
        String playerId = principal.playerId().toString();
        if (endDate != null && !endDate.isEmpty()) {
            return tournamentSearchService.findByDateRangeAndPlayerWithStatus(date, endDate, playerId);
        }
        return tournamentSearchService.findByDateAndPlayerWithStatus(date, playerId);
    }
}