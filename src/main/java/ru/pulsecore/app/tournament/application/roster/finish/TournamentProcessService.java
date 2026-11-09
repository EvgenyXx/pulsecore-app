package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.application.match.TournamentMatchService;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.shared.api.player.PlayerTournamentClient;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


//todo на удаление
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentProcessService {

    private final TournamentResultProcessor resultProcessor;
    private final PlayerTournamentClient playerClient;
    private final TournamentMatchService matchService;

    public List<TournamentResultEntity> processTournament(
            TournamentEntity tournament,
            ParsedResult parsed) {

        Set<UUID>playerIds = tournament.getNotifications()
                .stream().map(PlayerNotification::getPlayerId).collect(Collectors.toSet());


        List<PlayerData> roster = playerClient.getPlayerDataByIds(playerIds);

        Map<UUID, String> rosterData = roster.stream()
                .collect(Collectors.toMap(
                        PlayerData::id,
                        PlayerData::name
                ));



        List<TournamentResultEntity> resultEntities = processPlayerResults(rosterData, parsed, tournament);

        matchService.createMatches(parsed, tournament);



        return resultEntities;
    }

    private List<TournamentResultEntity> processPlayerResults(
            Map<UUID, String> roster,
            ParsedResult parsed,
            TournamentEntity tournament) {
        return resultProcessor.processResultsRoster(
                parsed.results(), roster, tournament,
                parsed.nightBonus(),
                parsed.hasRemoved(),
                parsed.league());
    }
}