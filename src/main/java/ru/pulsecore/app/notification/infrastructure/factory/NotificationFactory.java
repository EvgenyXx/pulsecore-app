package ru.pulsecore.app.notification.infrastructure.factory;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;

import java.util.UUID;

@Component
public class NotificationFactory {

    public PlayerNotification create(UUID player, TournamentEntity tournament, TournamentDto t) {
        PlayerNotification pn = new PlayerNotification();

        pn.setPlayerId(player);
        pn.setTournament(tournament);
        pn.setHall(t.getHallNumber());

        return pn;
    }
}