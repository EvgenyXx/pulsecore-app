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

    /**
     * Начало синхронизации при регистрации.
     */
    private static final LocalDate DEFAULT_FROM = LocalDate.of(2023, 1, 1);

    private final TournamentCascadeSyncService cascadeSyncService;

    @EventListener
    private void addTournaments(PlayerCreatedEvent event) {
        log.info("Началась асинхронная обработка для {}", event.playerName());
        try {
            LocalDate to = LocalDate.now().minusDays(1);   // вчера, сегодняшний день не считаем

            cascadeSyncService.syncPeriod(event.playerId(), event.playerName(), DEFAULT_FROM, to);
        } catch (Exception e) {
            log.error("Произошла ошибка при асинхронной обработке турниров {}", e.getMessage());
        }
    }
}