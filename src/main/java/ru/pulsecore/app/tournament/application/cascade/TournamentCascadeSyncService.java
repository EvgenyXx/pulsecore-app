package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.cache.SyncStateRepository;
import ru.pulsecore.app.tournament.infrastructure.cache.dto.SyncState;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentCascadeSyncService {


    private final SyncStateRepository stateRepository;
    private final TournamentSyncExecutor tournamentSyncExecutor;

    public void syncPeriod(UUID playerId, String playerName, LocalDate from, LocalDate to) {
        if (stateRepository.find(playerId).isPresent()) return;

        YearMonth currentMonth = YearMonth.from(from);
        YearMonth endMonth = YearMonth.from(to);

        stateRepository.save(playerId, new SyncState(playerName, currentMonth, endMonth, Instant.now()));
        tournamentSyncExecutor.runAsync(playerId, playerName, currentMonth, endMonth);
    }


}