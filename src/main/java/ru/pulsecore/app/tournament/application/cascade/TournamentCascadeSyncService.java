package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.shared.config.AsyncConfig;
import ru.pulsecore.app.tournament.infrastructure.util.MonthUtils;

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

    @Async(AsyncConfig.TASK_EXECUTOR)
    public void syncAllHistory(UUID playerId, String playerName) {
        if (!syncingPlayers.add(playerId)) {
            log.warn("{} — уже синхронизируется, пропускаем", playerName);
            return;
        }
        try {
            syncMonthsBackwards(playerId, playerName);
            log.info("{} — синхронизация завершена до {}", playerName, STOP_AT);
        } finally {
            syncingPlayers.remove(playerId);
        }
    }

    private void syncMonthsBackwards(UUID playerId, String playerName) {
        YearMonth month = YearMonth.now();

        while (!month.atDay(1).isBefore(STOP_AT)) {
            syncMonth(playerId, playerName, month);
            month = month.minusMonths(1);
            sleepBetweenMonths();
        }
    }

    private void syncMonth(UUID playerId, String playerName, YearMonth month) {
        try {
            LocalDate start = month.atDay(1);
            LocalDate end = month.atEndOfMonth();

            log.warn("{} — синхронизация {}", playerName, MonthUtils.toRussianMonthYear(start));
            tournamentAutoAddService.addTournamentsForPeriod(playerId, playerName, start, end);

        } catch (Exception e) {
            log.warn("{} — ошибка для {}: {}", playerName, month, e.getMessage());
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