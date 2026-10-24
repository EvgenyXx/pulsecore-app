package ru.pulsecore.app.tournament.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.event.PlayerCreatedEvent;
import ru.pulsecore.app.tournament.application.cascade.TournamentCascadeSyncService;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAddTournamentListener {

    private static final LocalDate DEFAULT_END = LocalDate.of(2023, 1, 1);

    private final TournamentCascadeSyncService cascadeSyncService;

    @EventListener
    private void addTournaments(PlayerCreatedEvent event) {
        log.info("Началась асинхронная обработка для {}", event.playerName());
        try {
            LocalDate from = LocalDate.now().minusDays(1); // вчера (старт)


            cascadeSyncService.syncPeriod(event.playerId(), event.playerName(), from, DEFAULT_END);
        } catch (Exception e) {
            log.error("Произошла ошибка при асинхронной обработке турниров {}", e.getMessage());
        }
    }
}