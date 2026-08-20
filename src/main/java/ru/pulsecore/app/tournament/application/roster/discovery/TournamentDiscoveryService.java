package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.TournamentDataProvider;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentDiscoveryService {

    private final PlayerClient playerClient;
    private final TournamentDataProvider tournamentDataProvider;
    private final TournamentFilter filter;
    private final TournamentSaver saver;
    private final NewTournamentEventPublisher publisher;

    public void checkNewTournaments() {
        List<PlayerData> activePlayers = playerClient.getAll();
        if (activePlayers.isEmpty()) {
            log.debug("Новые турниры: нет активных игроков");
            return;
        }

        log.debug("Новые турниры: проверка для {} игроков", activePlayers.size());

        Map<PlayerData, List<TournamentDto>> newTournaments = findNewTournaments(activePlayers);

        saver.saveAll(newTournaments);
        publisher.publish(newTournaments);

        logIfNotEmpty(newTournaments);
    }

    private Map<PlayerData, List<TournamentDto>> findNewTournaments(List<PlayerData> players) {
        List<String> playerNames = players.stream()
                .map(PlayerData::playerName)
                .toList();

        Map<String, List<TournamentDto>> allFound = tournamentDataProvider.findPlayerTournaments(playerNames);

        Map<PlayerData, List<TournamentDto>> result = new HashMap<>();
        for (PlayerData player : players) {
            List<TournamentDto> playerTournaments = allFound.getOrDefault(player.playerName(), List.of());
            List<TournamentDto> newOnes = filter.findNew(player.playerId(), playerTournaments);
            if (!newOnes.isEmpty()) {
                log.debug("Новые турниры: игрок={}, новых={}", player.playerName(), newOnes.size());
                result.put(player, newOnes);
            }
        }
        return result;
    }

    private void logIfNotEmpty(Map<PlayerData, List<TournamentDto>> notification) {
        if (!notification.isEmpty()) {
            log.info("Новые турниры: игроков={}, всего турниров={}",
                    notification.size(),
                    notification.values().stream().mapToInt(List::size).sum());
        }
    }
}