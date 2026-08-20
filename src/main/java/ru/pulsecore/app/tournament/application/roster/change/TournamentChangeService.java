package ru.pulsecore.app.tournament.application.roster.change;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.TournamentDataProvider;
import ru.pulsecore.app.tournament.application.roster.change.info.TournamentHashChecker;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;

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
    private final PlayerClient  playerClient;

    @Transactional
    public void checkChangedTournaments(){
        List<PlayerData> activePlayers = playerClient.getAll();

        if (activePlayers.isEmpty()) return;

        List<String> playerName = activePlayers.stream().map(PlayerData::playerName).toList();

        playerTournaments(playerName);
    }



    private   void playerTournaments(List<String>names){
        Map<String, List<TournamentDto>> all = tournamentDataProvider.getAllTournamentsFor3Days();


        Map<String, String> normalizedNames = names.stream()
                .collect(Collectors.toMap(NameNormalizer::normalizeForSearch, name -> name));

        Map<String, List<TournamentDto>> result = names.stream()
                .collect(Collectors.toMap(name -> name, name -> new ArrayList<>()));

        for (List<TournamentDto> dayTournaments : all.values()) {
            for (TournamentDto t : dayTournaments) {
                if (t.getPlayers() == null) continue;

                for (String player : t.getPlayers()) {
                    String normalized = NameNormalizer.normalizeForSearch(player);
                    String originalName = normalizedNames.get(normalized);

                    if (originalName != null) {
                        t.setHallNumber(NumberUtils.extractInt(t.getHall()));
                        result.get(originalName).add(t);

                    }
                }
            }

        }
        tournamentHashChecker.checkAndUpdateHashes(result,all);

    }
}
