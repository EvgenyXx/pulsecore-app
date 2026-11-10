package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.AsyncConfig;
import ru.pulsecore.app.shared.util.SleepUtils;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;




@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentCascadeSyncService {

    private final TournamentAutoAddService tournamentAutoAddService;
    private final Set<UUID> syncingPlayers = ConcurrentHashMap.newKeySet();

    @Async(AsyncConfig.TASK_EXECUTOR)
    public void syncPeriod(UUID playerId, String playerName, LocalDate from, LocalDate to) {
        if (!syncingPlayers.add(playerId)) {
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
     * Идём по месяцам от start (вчера) до end (2023) в обратном порядке.
     */
    private void syncMonthsBetween(UUID playerId, String playerName, LocalDate start, LocalDate end) {
        YearMonth currentMonth = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);

        while (!currentMonth.isBefore(endMonth)) {
            LocalDate monthStart = currentMonth.atDay(1);
            LocalDate monthEnd = currentMonth.atEndOfMonth();
            tournamentAutoAddService.addTournamentsForPeriod(playerId, playerName, monthStart, monthEnd);
            currentMonth = currentMonth.minusMonths(1);
            if (!currentMonth.isBefore(endMonth)) {
                SleepUtils.sleepSeconds(30);
            }
        }
    }



}