package ru.pulsecore.app.tournament.application.admin.assembler;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.PlayerEarning;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AdminTournamentAssembler {

    private record PlayerKey(Long tournamentId, UUID playerId) {}

    public List<AdminTournamentResponse> assemble(
            List<TournamentEntity> tournaments,
            List<PlayerData> players,
            List<TournamentResultEntity> results) {

        Map<UUID, String> names = players.stream()
                .collect(Collectors.toMap(PlayerData::id, PlayerData::name));

        Map<PlayerKey, Double> earnings = results.stream()
                .collect(Collectors.toMap(
                        r -> new PlayerKey(r.getTournament().getId(), r.getPlayerId()),
                        r -> r.getAmount() + r.getBonus(),
                        Double::sum
                ));

        return tournaments.stream()
                .map(t -> new AdminTournamentResponse(
                        t.getId(), t.getLink(), t.getDate(), t.getTime(),
                        t.isStarted(), t.isFinished(), t.isCancelled(), t.isProcessed(),
                        t.getNotifications().stream()
                                .map(pn -> new PlayerEarning(
                                        names.get(pn.getPlayerId()),
                                        earnings.getOrDefault(
                                                new PlayerKey(t.getId(), pn.getPlayerId()), 0.0)
                                ))
                                .toList()
                ))
                .toList();
    }
}