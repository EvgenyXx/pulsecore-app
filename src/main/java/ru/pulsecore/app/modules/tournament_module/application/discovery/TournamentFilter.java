package ru.pulsecore.app.modules.tournament_module.application.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentFilter {

    private final PlayerNotificationRepository notificationRepo;

    public List<TournamentDto> findNew(UUID playerId, List<TournamentDto> tournaments) {

        return tournaments.stream()
                .filter(t -> !notificationRepo
                        .existsByPlayerIdAndTournament_ExternalId(playerId, t.getId()))
                .toList();
    }
}