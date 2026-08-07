package ru.pulsecore.app.tournament.application.cascade;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.application.ResultService;
import ru.pulsecore.app.tournament.application.finish.TournamentResultProcessor;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentParseException;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import java.time.LocalDate;
import java.util.*;


/**
 * Обрабатывает список URL турниров для одного игрока.
 * Парсит каждый URL, находит/создает турнир, собирает результаты
 * и сохраняет их одним батчем в рамках одной транзакции.
 * Вызывается из TournamentAutoAddService при синхронизации истории.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentUrlProcessor {



    private final TournamentResultProcessor resultProcessor;
    private final ResultService resultService;
    private final TournamentRepository tournamentRepository;




    @Transactional
    public void processUrlsForPlayer(List<String> urls, UUID playerId, String playerName) {
        List<TournamentResultEntity> allEntities = new ArrayList<>();
        for (String url : urls) {
            try {
                ParsedResult parsed = parseUrl(url);
                TournamentEntity tournament = findOrCreateTournament(parsed, url);
                updateTournamentDates(tournament, parsed);

                allEntities.addAll(resultProcessor.processResults(
                        parsed.results(), playerId, playerName, tournament,
                        parsed.nightBonus(),
                        parsed.isFinished() || parsed.isFinalRemoved(),
                        parsed.hasRemoved(),
                        parsed.league()));
            } catch (Exception e) {
                log.warn("{} — {}", url, e.getMessage());
            }
        }

        resultProcessor.saveAll(allEntities);
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

}