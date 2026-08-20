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
    //todo добавить даты с какого и по какой или сделать вообще отдельные методы
    private static final LocalDate STOP_AT = LocalDate.of(2025, 1, 1);


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
            LocalDate end;

            if (month.equals(YearMonth.now())) {
                // Текущий месяц: с 1 числа по вчера
                end = LocalDate.now().minusDays(1);
            } else {
                // Прошлые месяцы: до конца месяца
                end = month.atEndOfMonth();
            }

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