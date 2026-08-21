package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentFilter {

    private final PlayerNotificationRepository notificationRepo;

    public List<TournamentDto> findNew(UUID playerId, List<TournamentDto> tournaments) {
        List<TournamentDto> newOnes = tournaments.stream()
                .filter(t -> !notificationRepo
                        .existsByPlayerIdAndTournament_ExternalId(playerId, t.getId()))
                .toList();

        if (!newOnes.isEmpty()) {
            log.debug("Фильтр: playerId={}, всего={}, новых={}",
                    playerId, tournaments.size(), newOnes.size());
        }

        return newOnes;
    }
}