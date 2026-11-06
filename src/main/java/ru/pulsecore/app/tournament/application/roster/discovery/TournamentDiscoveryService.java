package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.api.player.PlayerTournamentClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentDiscoveryService {

    private final PlayerTournamentClient playerClient;
    private final PlayerTournamentMatcher playerTournamentMatcher;
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

    }

    private Map<PlayerData, List<TournamentDto>> findNewTournaments(List<PlayerData> players) {
        List<String> playerNames = players.stream()
                .map(PlayerData::name)
                .toList();

        Map<String, List<TournamentDto>> allFound =
                playerTournamentMatcher.match(playerNames);

        Map<PlayerData, List<TournamentDto>> result = new HashMap<>();
        for (PlayerData player : players) {
            List<TournamentDto> playerTournaments = allFound.getOrDefault(player.name(), List.of());
            List<TournamentDto> newOnes = filter.findNew(player.id(), playerTournaments);
            if (!newOnes.isEmpty()) {
                log.debug("Новые турниры: игрок={}, новых={}", player.name(), newOnes.size());
                result.put(player, newOnes);
            }
        }
        return result;
    }


}