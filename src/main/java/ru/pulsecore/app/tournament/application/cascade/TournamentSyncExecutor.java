package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.AsyncConfig;
import ru.pulsecore.app.shared.util.SleepUtils;
import ru.pulsecore.app.tournament.infrastructure.cache.SyncStateRepository;
import ru.pulsecore.app.tournament.infrastructure.cache.dto.SyncState;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TournamentSyncExecutor {

    private static final AtomicInteger ACTIVE = new AtomicInteger();

    private final TournamentAutoAddService tournamentAutoAddService;
    private final SyncStateRepository stateRepository;

    public static int getActive() {
        return ACTIVE.get();
    }

    @Async(AsyncConfig.TASK_EXECUTOR)
    public void runAsync(UUID playerId, String playerName, YearMonth currentMonth, YearMonth endMonth) {
        ACTIVE.incrementAndGet();
        try {
            syncMonthsBetween(playerId, playerName, currentMonth, endMonth);
        } finally {
            stateRepository.delete(playerId);
            ACTIVE.decrementAndGet();
        }
    }

    public void syncMonthsBetween(UUID playerId, String playerName, YearMonth currentMonth, YearMonth endMonth) {
        Instant createdAt = stateRepository.find(playerId)
                .map(SyncState::createdAt)
                .orElse(Instant.now());

        while (!currentMonth.isBefore(endMonth)) {
            stateRepository.save(playerId, new SyncState(playerName, currentMonth, endMonth, createdAt));

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