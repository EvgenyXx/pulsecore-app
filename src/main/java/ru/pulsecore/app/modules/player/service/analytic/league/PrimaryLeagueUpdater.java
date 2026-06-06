// ==================== PrimaryLeagueUpdater.java ====================
package ru.pulsecore.app.modules.player.service.analytic.league;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrimaryLeagueUpdater {

    private final PlayerRepository playerRepository;
    private final TournamentResultRepository tournamentResultRepository;
    private final EntityManager entityManager;

    @PostConstruct
    public void init() {
        updateAllPrimaryLeagues();
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void updateAllPrimaryLeagues() {
        List<Player> players = playerRepository.findAll();
        for (Player player : players) {
            String primary = tournamentResultRepository.findPrimaryLeague(player.getId());
            if (primary != null) {
                player.setPrimaryLeague(primary);
                playerRepository.save(player);
            }
        }
        log.info("Обновлены основные лиги для {} игроков", players.size());

        // Обновляем вьюху топа
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW top_players_view").executeUpdate();
        log.info("Обновлена вьюха top_players_view");
    }
}