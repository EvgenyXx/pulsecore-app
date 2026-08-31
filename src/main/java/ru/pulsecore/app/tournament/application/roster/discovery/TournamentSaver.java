package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentSaver {

    private final TournamentResolver tournamentResolver;
    private final NotificationBuilder notificationBuilder;
    private final TournamentBatchSaver batchSaver;

    @Transactional
    public void saveAll(Map<PlayerData, List<TournamentDto>> data) {
        log.debug("Сохранение: игроков={}, всего турниров={}",
                data.size(),
                data.values().stream().mapToInt(List::size).sum());

        List<TournamentEntity> newTournaments = new ArrayList<>();
        List<TournamentEntity> updatedTournaments = new ArrayList<>();
        List<PlayerNotification> allNotifications = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        for (var entry : data.entrySet()) {
            PlayerData player = entry.getKey();
            for (TournamentDto t : entry.getValue()) {
                TournamentEntity tournament = tournamentResolver.resolveTournament(
                        t, seen, newTournaments, updatedTournaments);

                notificationBuilder.buildNotificationIfNeeded(
                        player, tournament, t, allNotifications);
            }
        }

        batchSaver.saveAll(newTournaments, updatedTournaments, allNotifications);
    }
}