package ru.pulsecore.app.modules.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.core.dto.ResultDto;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.shared.exception.PlayerNotFoundException;
import ru.pulsecore.app.modules.shared.exception.TournamentParseException;
import ru.pulsecore.app.modules.shared.exception.UnauthorizedException;
import ru.pulsecore.app.modules.tournament.api.dto.AddTournamentResponse;
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
public class TournamentProcessService {

    private final TournamentResultService tournamentResultService;
    private final ResultService resultService;
    private final TournamentRepository tournamentRepository;
    private final PlayerService playerService;

    private static final long REQUEST_DELAY_MS = 3000;

    @Transactional
    public void processTournament(List<PlayerNotification> notifications, ParsedResult parsed) {
        if (notifications == null || notifications.isEmpty()) {
            log.warn("⏭ processTournament skip: empty notifications");
            return;
        }
        TournamentEntity tournament = notifications.get(0).getTournament();
        if (tournament == null) {
            log.warn("⏭ processTournament skip: tournament is null");
            return;
        }
        log.info("🏁 process finish: tournamentId={}, users={}", parsed.tournamentId(), notifications.size());

        int processed = 0, foundCount = 0;
        List<ResultDto> resultDto = parsed.results();

        // Обновим дату и время турнира если их нет
        if (tournament.getDate() == null && !parsed.results().isEmpty()) {
            String dateStr = parsed.results().get(0).getDate();
            if (dateStr != null && !dateStr.isEmpty()) {
                try { tournament.setDate(LocalDate.parse(dateStr)); } catch (Exception ignored) {}
            }
        }
        if (tournament.getTime() == null && parsed.time() != null) {
            tournament.setTime(parsed.time());
        }

        for (PlayerNotification pn : notifications) {
            Player player = pn.getPlayer();
            if (player == null) continue;
            processed++;
            boolean found = tournamentResultService.processResults(
                    resultDto, player, parsed.tournamentId(),
                    parsed.nightBonus(),
                    parsed.isFinished() || parsed.isFinalRemoved(),
                    parsed.hasRemoved(),
                    parsed.league());
            if (found) foundCount++;
        }
        tournament.setFinished(true);
        log.info("✅ process finish done: tournamentId={}, processed={}, found={}", tournament.getExternalId(), processed, foundCount);
    }

    public AddTournamentResponse processByUrl(String url, String playerId) {
        return processSingleUrl(url, playerId);
    }

    public List<AddTournamentResponse> processByUrls(List<String> urls, String playerId) {
        if (playerId == null) throw new UnauthorizedException();
        Player player = playerService.findById(UUID.fromString(playerId));
        if (player == null) throw new PlayerNotFoundException(playerId);
        List<AddTournamentResponse> responses = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            try {
                responses.add(processSingleUrl(url, playerId));
                if (i < urls.size() - 1) {
                    try { Thread.sleep(REQUEST_DELAY_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            } catch (Exception e) {
                log.error("❌ Ошибка обработки URL: {}", url, e);
                responses.add(AddTournamentResponse.builder().message("Ошибка: " + e.getMessage()).resultsCount(0).results(List.of()).build());
            }
        }
        return responses;
    }

    private AddTournamentResponse processSingleUrl(String url, String playerId) {
        Player player = playerService.findById(UUID.fromString(playerId));
        if (player == null) throw new PlayerNotFoundException(playerId);

        ParsedResult parsed;
        try {
            parsed = resultService.calculateAll(url);
        } catch (Exception e) {
            throw new TournamentParseException(url, e);
        }

        LocalDate tournamentDate = null;
        if (!parsed.results().isEmpty()) {
            String dateStr = parsed.results().get(0).getDate();
            if (dateStr != null && !dateStr.isEmpty()) {
                try { tournamentDate = LocalDate.parse(dateStr); } catch (Exception ignored) {}
            }
        }

        // Найти или создать турнир
        TournamentEntity tournament = tournamentRepository.findByExternalId(parsed.tournamentId())
                .orElseGet(() -> tournamentRepository.save(TournamentEntity.builder()
                        .externalId(parsed.tournamentId())
                        .link(url)
                        .build()));

        // Обновить дату и время, если их нет
        if (tournament.getDate() == null && tournamentDate != null) {
            tournament.setDate(tournamentDate);
        }
        if (tournament.getTime() == null && parsed.time() != null && !parsed.time().isEmpty()) {
            tournament.setTime(parsed.time());
        }
        tournamentRepository.save(tournament);

        tournamentResultService.processResults(
                parsed.results(), player, parsed.tournamentId(),
                parsed.nightBonus(),
                parsed.isFinished() || parsed.isFinalRemoved(),
                parsed.hasRemoved(),
                parsed.league());

        return AddTournamentResponse.builder()
                .message("Турнир обработан")
                .tournamentId(parsed.tournamentId())
                .resultsCount(parsed.results().size())
                .results(parsed.results())
                .build();
    }//todo его менять на старый
}