// ==================== TournamentCascadeSyncService.java ====================
package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;

import java.time.LocalDate;
import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentCascadeSyncService {

    private static final LocalDate STOP_AT = LocalDate.of(2025, 1, 1);
    private static final long MONTH_DELAY_MS = 60_000; // 1 минута между месяцами

    private final TournamentAutoAddService tournamentAutoAddService;

    @Async("taskExecutor")
    public void syncAllHistory(Player player) {
        YearMonth month = YearMonth.now().minusMonths(1);

        while (!month.atDay(1).isBefore(STOP_AT)) {
            try {
                LocalDate start = month.atDay(1);
                LocalDate end = month.atEndOfMonth();

                log.info("{} — синхронизация {} - {}", player.getName(), start, end);
                int added = tournamentAutoAddService.addTournamentsForPeriod(player, start, end);

                log.info("{} — месяц {} готов, турниров: {}", player.getName(), month, added);

                Thread.sleep(MONTH_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("{} — ошибка для {}: {}", player.getName(), month, e.getMessage());
            }
            month = month.minusMonths(1);
        }

        log.info("{} — синхронизация завершена до {}", player.getName(), STOP_AT);
    }
}