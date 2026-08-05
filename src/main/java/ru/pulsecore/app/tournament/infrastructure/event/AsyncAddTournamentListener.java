package ru.pulsecore.app.tournament.infrastructure.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.PlayerCreatedEvent;
import ru.pulsecore.app.tournament.application.tournament.TournamentAutoAddService;
import ru.pulsecore.app.tournament.application.tournament.TournamentCascadeSyncService;



@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAddTournamentListener {


    private final TournamentAutoAddService tournamentAutoAddService;
    private final TournamentCascadeSyncService cascadeSyncService;

    @EventListener
    private void addTournaments(PlayerCreatedEvent event) {
        log.info("Началась асинхронная обработка для {}",event.playerName());
        try {
            tournamentAutoAddService.addRecentTournamentsForPlayer(
                    event.playerId(), event.playerName(), event.days());
            cascadeSyncService.syncAllHistory(event.playerId(), event.playerName());
        }catch (Exception e) {
            log.error("Произошла ошибка при асинхронном обработке турниров {}",e.getMessage());
        }

    }


}
