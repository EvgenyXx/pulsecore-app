package ru.pulsecore.app.tournament.application.roster.change;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.TournamentDataProvider;
import ru.pulsecore.app.tournament.application.roster.change.info.TournamentHashChecker;
import ru.pulsecore.app.shared.api.player.PlayerTournamentClient;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerIdentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class TournamentChangeService {

    private final TournamentDataProvider tournamentDataProvider;
    private final TournamentHashChecker tournamentHashChecker;
    private final PlayerTournamentClient playerClient;

    @Transactional
    public void checkChangedTournaments() {
        List<PlayerData> activePlayers = playerClient.getAll();

        if (activePlayers.isEmpty()) {
            log.debug("Изменения: нет активных игроков");
            return;
        }

        log.debug("Изменения: проверка для {} игроков", activePlayers.size());

        List<String> playerName = activePlayers.stream().map(PlayerData::name).toList();

        playerTournaments(playerName);
    }

    private void playerTournaments(List<String> names) {
        Map<String, List<TournamentDto>> all = tournamentDataProvider.getAllTournamentsFor3Days();

        Map<String, String> normalizedNames = names.stream()
                .collect(Collectors.toMap(PlayerIdentity::cleanName,
                        name -> name, (n1, n2) -> n1));

        Map<String, List<TournamentDto>> result = names.stream()
                .collect(Collectors.toMap(name ->
                        name, name -> new ArrayList<>(),
                        (n1, n2) -> n1));

        List<TournamentDto> dayTournaments = all.values().stream()
                .flatMap(List::stream)
                .toList();

        for (TournamentDto t : dayTournaments) {
            if (t.getPlayers() == null) continue;
            t.setHallNumber(NumberUtils.extractInt(t.getHall()));
            for (String player : t.getPlayers()) {
                String normalized = PlayerIdentity.cleanName(player);
                String originalName = normalizedNames.get(normalized);
                if (originalName != null) {
                    result.get(originalName).add(t);
                }
            }
        }

        tournamentHashChecker.checkAndUpdateHashes(result, all);
        log.debug("Изменения: проверка завершена");
    }
}