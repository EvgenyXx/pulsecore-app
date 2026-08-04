package ru.pulsecore.app.modules.notification_modules.infrastructure.factory;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.entity.PlayerNotification;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.entity.TournamentEntity;

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