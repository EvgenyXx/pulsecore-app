package ru.pulsecore.app.modules.notification.factory;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentEntity;

@Component
public class NotificationFactory {

    public PlayerNotification create(Player player, TournamentEntity tournament, TournamentDto t) {
        PlayerNotification pn = new PlayerNotification();

        pn.setPlayer(player);
        pn.setTournament(tournament);
        pn.setHall(t.getHallNumber());

        return pn;
    }
}