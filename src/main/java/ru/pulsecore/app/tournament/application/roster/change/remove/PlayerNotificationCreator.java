package ru.pulsecore.app.tournament.application.roster.change.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerNotificationCreator {

    private final TournamentRepository tournamentRepository;
    private final PlayerNotificationRepository notificationRepository;

    public void createNotificationForTransfer(PlayerData player, TournamentDto to) {
        TournamentEntity newTournamentEntity = tournamentRepository
                .findByLink(to.getLink())
                .orElse(null);

        if (newTournamentEntity == null) {
            log.warn("Турнир для переноса не найден: {}", to.getLink());
            return;
        }

        PlayerNotification pn = PlayerNotification.builder()
                .playerId(player.playerId())
                .tournament(newTournamentEntity)
                .hall(NumberUtils.extractInt(to.getHall()))
                .build();

        notificationRepository.save(pn);
        log.debug("Создана новая связь: player={}, tournament={}",
                player.playerName(), newTournamentEntity.getExternalId());
    }
}