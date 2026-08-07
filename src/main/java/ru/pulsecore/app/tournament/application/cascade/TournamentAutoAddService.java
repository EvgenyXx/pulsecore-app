
package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Ищет турниры игрока за указанный период через MastersAPI
 * и передает их в TournamentUrlProcessor для сохранения результатов.
 * Используется CascadeSyncService для синхронизации истории.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentAutoAddService {

    private final TournamentSearchService tournamentSearchService;
    private final TournamentUrlProcessor tournamentUrlProcessor;


    public void addTournamentsForPeriod(UUID playerId, String playerName, LocalDate start, LocalDate end) {
        List<TournamentDto> tournaments;
        try {
            tournaments = tournamentSearchService.findByDateRangeAndPlayer(
                    start.toString(), end.toString(), playerName);
        } catch (Exception e) {
            log.error("Ошибка поиска турниров для {}: {}", playerName, e.getMessage());
            throw new RuntimeException(e);
        }


        List<String>urls = new ArrayList<>();
        for (TournamentDto t : tournaments) {
            urls.add(t.getLink());
        }
        tournamentUrlProcessor.processUrlsForPlayer(urls, playerId,playerName);


    }
}