package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentCascadeSyncService {

    private static final LocalDate STOP_AT = LocalDate.of(2025, 1, 1);
    private static final long MONTH_DELAY_MS = 60_000;

    private final TournamentAutoAddService tournamentAutoAddService;
    private final Set<UUID> syncingPlayers = ConcurrentHashMap.newKeySet();

    @Async("taskExecutor")
    public void syncAllHistory(Player player) {
        if (!syncingPlayers.add(player.getId())) {
            log.warn("{} — уже синхронизируется, пропускаем", player.getName());
            return;
        }
        try {
            syncMonthsBackwards(player);
            log.info("{} — синхронизация завершена до {}", player.getName(), STOP_AT);
        } finally {
            syncingPlayers.remove(player.getId());
        }
    }

    private void syncMonthsBackwards(Player player) {
        YearMonth month = YearMonth.now().minusMonths(1);

        while (!month.atDay(1).isBefore(STOP_AT)) {
            syncMonth(player, month);
            month = month.minusMonths(1);
            sleepBetweenMonths();
        }
    }

    private void syncMonth(Player player, YearMonth month) {
        try {
            LocalDate start = month.atDay(1);
            LocalDate end = month.atEndOfMonth();

            log.info("{} — синхронизация {} - {}", player.getName(), start, end);
            int added = tournamentAutoAddService.addTournamentsForPeriod(player, start, end);
            log.info("{} — месяц {} готов, турниров: {}", player.getName(), month, added);
        } catch (Exception e) {
            log.warn("{} — ошибка для {}: {}", player.getName(), month, e.getMessage());
        }
    }

    private void sleepBetweenMonths() {
        try {
            Thread.sleep(MONTH_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}