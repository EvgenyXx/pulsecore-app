package ru.pulsecore.app.tournament.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.event.PlayerCreatedEvent;
import ru.pulsecore.app.tournament.application.cascade.TournamentCascadeSyncService;
import ru.pulsecore.app.tournament.infrastructure.properties.TournamentSettingProperties;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAddTournamentListener {

    private final TournamentSettingProperties settingProperties;
    private final TournamentCascadeSyncService cascadeSyncService;

    @EventListener
    private void addTournaments(PlayerCreatedEvent event) {
        log.info("Началась асинхронная обработка для {}", event.playerName());
        LocalDate from = LocalDate.now().minusDays(settingProperties.playerHistory().start());
        cascadeSyncService.syncPeriod(event.playerId(), event.playerName(),
                                        from, settingProperties.playerHistory().end());

    }
}