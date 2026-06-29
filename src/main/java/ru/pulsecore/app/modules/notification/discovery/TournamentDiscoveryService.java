package ru.pulsecore.app.modules.notification.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentDiscoveryService {

    private final PlayerService userService;
    private final TournamentFinder finder;
    private final TournamentFilter filter;
    private final TournamentSaver saver;
    private final DiscoveryNotificationService notificationService;

    public void checkNewTournaments(UUID playerId) {
        Player user = userService.findById(playerId);
        if (user == null) {
            log.warn("Player not found: {}", playerId);
            return;
        }

        List<TournamentDto> newTournaments = findNewTournaments(user);
        if (newTournaments.isEmpty()) return;

        saver.save(user, newTournaments);
        notificationService.sendNotifications(user, newTournaments);
        log.info("📧📲 Sent {} notifications to {}", newTournaments.size(), user.getEmail());
    }

    private List<TournamentDto> findNewTournaments(Player user) {
        List<TournamentDto> tournaments = finder.find(user);
        if (tournaments.isEmpty()) return List.of();
        return filter.findNew(user, tournaments);
    }
}