package ru.pulsecore.app.tournament.application.cascade;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.cache.SyncStateRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentSyncRecoveryService {

    private final SyncStateRepository stateRepository;
    private final TournamentSyncExecutor tournamentSyncExecutor;

    @PostConstruct
    public void recover() {
        List<UUID> ids = stateRepository.findAllSortedByCreatedAt();
        if (ids.isEmpty()) return;

        log.info("Восстановление синхронизации: {} игроков", ids.size());

        for (UUID playerId : ids) {
            stateRepository.find(playerId).ifPresent(state ->
                    tournamentSyncExecutor.runAsync(
                            playerId, state.playerName(), state.currentMonth(), state.endMonth())
            );
        }
    }
}