
package ru.pulsecore.app.tournament.application.tournament;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.config.AsyncConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentAutoAddService {

    private final TournamentSearchService tournamentSearchService;
    private final TournamentUrlProcessor tournamentUrlProcessor;

    @Async(AsyncConfig.TASK_EXECUTOR)
    public void addRecentTournamentsForPlayer(UUID playerId, String playerName, int days) {
        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = LocalDate.now();
        addTournamentsForPeriod(playerId,playerName, start, end);
    }

    public int addTournamentsForPeriod(UUID playerId, String playerName, LocalDate start, LocalDate end) {
        List<TournamentDto> tournaments;
        try {
            tournaments = tournamentSearchService.findByDateRangeAndPlayer(
                    start.toString(), end.toString(), playerName);
        } catch (Exception e) {
            log.error("Ошибка поиска турниров для {}: {}", playerName, e.getMessage());
            return 0;
        }

        int added = 0;
        for (TournamentDto t : tournaments) {
            try {
                tournamentUrlProcessor.processByUrl(t.getLink(), playerId);
                added++;
            } catch (Exception e) {
                log.warn("{} — {}", t.getLink(), e.getMessage());
            }
        }

        return added;
    }
}