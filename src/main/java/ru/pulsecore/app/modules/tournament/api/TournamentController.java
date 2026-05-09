package ru.pulsecore.app.modules.tournament.api;

import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.notification.service.TournamentProcessService;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.modules.tournament.api.dto.AddTournamentRequest;
import ru.pulsecore.app.modules.tournament.api.dto.AddTournamentResponse;
import ru.pulsecore.app.modules.tournament.api.dto.TournamentSearchResult;
import ru.pulsecore.app.modules.tournament.service.TournamentSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentProcessService tournamentProcessService;
    private final TournamentSearchService tournamentSearchService;
    private final TournamentResultService tournamentResultService;

    private String getPlayerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getPlayerId();
        }
        return null;
    }

    private String getPlayerName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getPlayerName();
        }
        return null;
    }

    @PostMapping(TournamentApi.ADD)
    public ResponseEntity<AddTournamentResponse> addByUrl(@Valid @RequestBody AddTournamentRequest request) {
        String playerId = getPlayerId();
        if (playerId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(tournamentProcessService.processByUrl(request.getUrl(), playerId));
    }

    @GetMapping(TournamentApi.SEARCH)
    public ResponseEntity<List<TournamentDto>> searchTournaments(
            @RequestParam(TournamentApi.PARAM_DATE) String date,
            @RequestParam(required = false) String endDate) {
        String playerName = getPlayerName();
        if (playerName == null) return ResponseEntity.status(401).build();
        if (endDate != null && !endDate.isEmpty()) {
            return ResponseEntity.ok(tournamentSearchService.findByDateRangeAndPlayer(date, endDate, playerName));
        }
        return ResponseEntity.ok(tournamentSearchService.findByDateAndPlayer(date, playerName));
    }

    @PostMapping(TournamentApi.ADD_BATCH)
    public ResponseEntity<List<AddTournamentResponse>> addByUrls(@Valid @RequestBody List<AddTournamentRequest> requests) {
        String playerId = getPlayerId();
        if (playerId == null) return ResponseEntity.status(401).build();
        List<String> urls = requests.stream().map(AddTournamentRequest::getUrl).toList();
        return ResponseEntity.ok(tournamentProcessService.processByUrls(urls, playerId));
    }

    @PutMapping(TournamentApi.UPDATE_RESULT)
    public ResponseEntity<Map<String, String>> updateResult(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        if (getPlayerId() == null) return ResponseEntity.status(401).build();
        tournamentResultService.updateResult(id,
                body.get(TournamentApi.PARAM_AMOUNT),
                body.get(TournamentApi.PARAM_BONUS));
        return ResponseEntity.ok(Map.of(TournamentApi.RESP_MESSAGE, TournamentApi.RESP_OK));
    }

    @GetMapping(TournamentApi.SEARCH_WITH_STATUS)
    public ResponseEntity<List<TournamentSearchResult>> searchTournamentsWithStatus(
            @RequestParam(TournamentApi.PARAM_DATE) String date,
            @RequestParam(required = false) String endDate) {
        String playerId = getPlayerId();
        if (playerId == null) return ResponseEntity.status(401).build();
        if (endDate != null && !endDate.isEmpty()) {
            return ResponseEntity.ok(tournamentSearchService.findByDateRangeAndPlayerWithStatus(date, endDate, playerId));
        }
        return ResponseEntity.ok(tournamentSearchService.findByDateAndPlayerWithStatus(date, playerId));
    }
}