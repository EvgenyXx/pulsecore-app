// TournamentUrlProcessor.java
package ru.pulsecore.app.modules.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.shared.exception.PlayerNotFoundException;
import ru.pulsecore.app.modules.shared.exception.TournamentParseException;
import ru.pulsecore.app.modules.shared.exception.UnauthorizedException;
import ru.pulsecore.app.modules.tournament.api.dto.response.AddTournamentResponse;
import ru.pulsecore.app.modules.tournament.application.ResultService;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentEntity;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentUrlProcessor {

    private static final long REQUEST_DELAY_MS = 3000;

    private final TournamentResultService tournamentResultService;
    private final ResultService resultService;
    private final TournamentRepository tournamentRepository;
    private final PlayerService playerService;

    public void processByUrl(String url, String playerId) {
        processSingleUrl(url, playerId);
    }

    public List<AddTournamentResponse> processByUrls(List<String> urls, String playerId) {
        if (playerId == null) throw new UnauthorizedException();
        findPlayer(playerId);

        List<AddTournamentResponse> responses = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            try {
                responses.add(processSingleUrl(url, playerId));
            } catch (Exception e) {
                log.error("❌ Ошибка обработки URL: {}", url, e);
                responses.add(buildErrorResponse(e));
            }
            delayIfNotLast(i, urls.size());
        }
        return responses;
    }

    private AddTournamentResponse processSingleUrl(String url, String playerId) {
        Player player = findPlayer(playerId);
        ParsedResult parsed = parseUrl(url);
        TournamentEntity tournament = findOrCreateTournament(parsed, url);
        updateTournamentDates(tournament, parsed);

        tournamentResultService.processResults(
                parsed.results(), player, parsed.tournamentId(),
                parsed.nightBonus(),
                parsed.isFinished() || parsed.isFinalRemoved(),
                parsed.hasRemoved(),
                parsed.league());

        return buildSuccessResponse(parsed);
    }

    private Player findPlayer(String playerId) {
        Player player = playerService.findById(UUID.fromString(playerId));
        if (player == null) throw new PlayerNotFoundException(playerId);
        return player;
    }

    private ParsedResult parseUrl(String url) {
        try {
            return resultService.calculateAll(url);
        } catch (Exception e) {
            throw new TournamentParseException(url, e);
        }
    }

    private TournamentEntity findOrCreateTournament(ParsedResult parsed, String url) {
        return tournamentRepository.findByExternalId(parsed.tournamentId())
                .orElseGet(() -> tournamentRepository.save(TournamentEntity.builder()
                        .externalId(parsed.tournamentId())
                        .link(url)
                        .build()));
    }

    private void updateTournamentDates(TournamentEntity tournament, ParsedResult parsed) {
        if (tournament.getDate() == null) {
            tournament.setDate(extractDate(parsed));
        }
        if (tournament.getTime() == null && parsed.time() != null && !parsed.time().isEmpty()) {
            tournament.setTime(parsed.time());
        }
        tournamentRepository.save(tournament);
    }

    private LocalDate extractDate(ParsedResult parsed) {
        if (parsed.results().isEmpty()) return null;
        String dateStr = parsed.results().get(0).getDate();
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private void delayIfNotLast(int index, int total) {
        if (index < total - 1) {
            try { Thread.sleep(REQUEST_DELAY_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    private AddTournamentResponse buildSuccessResponse(ParsedResult parsed) {
        return AddTournamentResponse.builder()
                .message("Турнир обработан")
                .tournamentId(parsed.tournamentId())
                .resultsCount(parsed.results().size())
                .results(parsed.results())
                .build();
    }

    private AddTournamentResponse buildErrorResponse(Exception e) {
        return AddTournamentResponse.builder()
                .message("Ошибка: " + e.getMessage())
                .resultsCount(0)
                .results(List.of())
                .build();
    }
}