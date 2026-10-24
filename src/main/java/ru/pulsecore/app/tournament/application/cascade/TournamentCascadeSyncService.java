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
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentCascadeSyncService {

    private final TournamentAutoAddService tournamentAutoAddService;
    private final Set<UUID> syncingPlayers = ConcurrentHashMap.newKeySet();

    @Async(AsyncConfig.TASK_EXECUTOR)
    public void syncPeriod(UUID playerId, String playerName, LocalDate from, LocalDate to) {
        if (!syncingPlayers.add(playerId)) {
            log.warn("{} — уже синхронизируется, пропускаем", playerName);
            return;
        }
        try {
            syncMonthsBetween(playerId, playerName, from, to);
            log.info("{} — синхронизация завершена: {} – {}", playerName, from, to);
        } finally {
            syncingPlayers.remove(playerId);
        }
    }

    /**
     * Идём по месяцам от from (вчера) до to (2023) в обратном порядке.
     */
    private void syncMonthsBetween(UUID playerId, String playerName, LocalDate from, LocalDate to) {
        YearMonth month = YearMonth.from(from);
        YearMonth endMonth = YearMonth.from(to);

        while (!month.isBefore(endMonth)) {
            syncMonth(playerId, playerName, month, from, to);
            month = month.minusMonths(1);
            if (!month.isBefore(endMonth)) {
                sleepBetweenMonths();
            }
        }
    }

    private void syncMonth(UUID playerId, String playerName, YearMonth month, LocalDate from, LocalDate to) {
        try {
            LocalDate start = month.atDay(1);
            LocalDate end = month.atEndOfMonth();

            if (start.isBefore(to)) start = to;
            if (end.isAfter(from)) end = from;
            if (start.isAfter(end)) return;

            log.debug("{} — синхронизация {}", playerName, MonthUtils.toRussianMonthYear(start));
            tournamentAutoAddService.addTournamentsForPeriod(playerId, playerName, start, end);
        } catch (Exception e) {
            log.warn("{} — ошибка для {}: {}", playerName, month, e.getMessage());
        }
    }

    private void sleepBetweenMonths() {
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}