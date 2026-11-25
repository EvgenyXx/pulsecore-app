package ru.pulsecore.app.tournament.application.roster.canceled;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.api.player.PlayerTournamentClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.infrastructure.audit.AuditWriter;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TournamentCanceledAuditor {

    private final PlayerTournamentClient playerClient;
    private final AuditWriter auditWriter;
    private final TournamentRepository tournamentRepository;

    public void logCancelled(String url) {
        tournamentRepository.findByLink(url).ifPresent(tournament -> {
            Set<UUID> playerIds = tournament.getNotifications().stream()
                    .map(PlayerNotification::getPlayerId)
                    .collect(Collectors.toSet());

            List<PlayerData> players = playerClient.getPlayerDataByIds(playerIds);
            List<String> names = players.stream().map(PlayerData::name).toList();

            auditWriter.write("cancelled",
                    "турнир=" + tournament.getExternalId() +
                            " | link=" + tournament.getLink() +
                            " | игроков=" + names);
        });
    }
}