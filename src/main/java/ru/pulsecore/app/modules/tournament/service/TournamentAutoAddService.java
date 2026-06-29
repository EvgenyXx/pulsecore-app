// ==================== TournamentAutoAddService.java ====================
package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.notification.service.TournamentUrlProcessor;
import ru.pulsecore.app.modules.player.domain.Player;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentAutoAddService {

    private final TournamentSearchService tournamentSearchService;
    private final TournamentUrlProcessor tournamentUrlProcessor;

    @Async("taskExecutor")
    public void addRecentTournamentsForPlayer(Player player, int days) {
        LocalDate start = LocalDate.now().minusDays(days);
        LocalDate end = LocalDate.now();
        addTournamentsForPeriod(player, start, end);
    }

    public int addTournamentsForPeriod(Player player, LocalDate start, LocalDate end) {
        List<TournamentDto> tournaments;
        try {
            tournaments = tournamentSearchService.findByDateRangeAndPlayer(
                    start.toString(), end.toString(), player.getName());
        } catch (Exception e) {
            log.error("Ошибка поиска турниров для {}: {}", player.getName(), e.getMessage());
            return 0;
        }

        int added = 0;
        for (TournamentDto t : tournaments) {
            try {
                tournamentUrlProcessor.processByUrl(t.getLink(), player.getId().toString());
                added++;
            } catch (Exception e) {
                log.warn("{} — {}", t.getLink(), e.getMessage());
            }
        }

        return added;
    }
}