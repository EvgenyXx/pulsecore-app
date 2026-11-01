package ru.pulsecore.app.tournament.application.roster.change;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.infrastructure.audit.AuditWriter;
import ru.pulsecore.app.tournament.application.roster.change.remove.PlayerRemovalDetector;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentRosterAuditService {

    private final PlayerRemovalDetector playerRemovalDetector;
    private final AuditWriter auditWriter;

    public void logReplacement(List<PlayerData> oldPlayers, TournamentDto newTournament, List<String> removedNames) {
        List<String> addedNames = playerRemovalDetector.findAddedNames(oldPlayers, newTournament);

        auditWriter.write("changes", String.format(
                "link=%s | ушел=%s | пришел=%s | было=%s | стало=%s",
                newTournament.getLink(), removedNames, addedNames,
                oldPlayers.stream().map(PlayerData::name).toList(),
                newTournament.getPlayers()
        ));

        log.info("Замена: турнир={} | link={} | ушел={} | пришел={}",
                newTournament.getId(), newTournament.getLink(), removedNames, addedNames);
    }
}