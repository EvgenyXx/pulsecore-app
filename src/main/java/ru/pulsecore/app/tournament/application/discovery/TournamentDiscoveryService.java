package ru.pulsecore.app.tournament.application.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.TournamentDto;

import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.application.tournament.TournamentSaver;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentDiscoveryService {

    private final PlayerClient playerClient;
    private final TournamentFinder finder;
    private final TournamentFilter filter;
    private final TournamentSaver saver;
    private final DiscoveryNotificationService notificationService;

    public void checkNewTournaments(UUID playerId) {
        var player = playerClient.getPlayerById(playerId);


        List<TournamentDto> newTournaments =
                findNewTournaments(player.playerId(), player.playerName());
        if (newTournaments.isEmpty()) return;

        saver.save(player.playerId(), newTournaments);
        notificationService.sendNotifications(
                player.playerId(),
                player.playerName(),
                player.email(),
                newTournaments);
        log.info("📧📲 Sent {} notifications to {}", newTournaments.size(),player.email());
    }

    private List<TournamentDto> findNewTournaments(UUID playerId,String name) {
        List<TournamentDto> tournaments = finder.find(name);
        if (tournaments.isEmpty()) return List.of();
        return filter.findNew(playerId, tournaments);
    }
}