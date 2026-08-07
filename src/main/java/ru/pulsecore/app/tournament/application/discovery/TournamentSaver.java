package ru.pulsecore.app.tournament.application.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.notification.infrastructure.factory.NotificationFactory;
import ru.pulsecore.app.notification.infrastructure.factory.TournamentFactory;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentSaver {

    private final TournamentRepository tournamentRepository;
    private final NotificationFactory notificationFactory;
    private final PlayerNotificationRepository notificationRepo;
    private final TournamentFactory tournamentFactory;

    public void saveAll(Map<PlayerData, List<TournamentDto>> data) {
        List<TournamentEntity> newTournaments = new ArrayList<>();
        List<PlayerNotification> allNotifications = new ArrayList<>();

        // Сначала собираем все новые турниры
        for (var entry : data.entrySet()) {
            PlayerData player = entry.getKey();
            for (TournamentDto t : entry.getValue()) {
                TournamentEntity tournament = tournamentRepository
                        .findByExternalId(t.getId())
                        .orElseGet(() -> {
                            TournamentEntity newT = tournamentFactory.create(t);
                            newTournaments.add(newT);
                            return newT;
                        });
                allNotifications.add(notificationFactory.create(player.playerId(), tournament, t));
            }
        }

        // Батчем сохраняем новые турниры
        if (!newTournaments.isEmpty()) {
            tournamentRepository.saveAll(newTournaments);
        }

        // Батчем сохраняем уведомления
        notificationRepo.saveAll(allNotifications);
    }
}