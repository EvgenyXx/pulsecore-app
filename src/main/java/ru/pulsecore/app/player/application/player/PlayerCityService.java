package ru.pulsecore.app.player.application.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.enums.City;
import ru.pulsecore.app.player.infrastructure.repository.PlayerRepository;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerCityService {

    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;

    @Transactional
    public void syncUpcomingCities() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);

        List<TournamentEntity> tournaments = tournamentRepository.findTournamentsBetweenDates(startDate, endDate);

        Map<UUID, City> playerCityMap = new HashMap<>();

        for (TournamentEntity tournament : tournaments) {
             if (tournament.getHallNumber() == null) {
                continue;
            }
            City city = City.fromHallNumber(tournament.getHallNumber());

            for (PlayerNotification notification : tournament.getNotifications()) {
                playerCityMap.put(notification.getPlayerId(), city);
            }
        }

        List<Player> players = playerRepository.findAllByIdIn(playerCityMap.keySet());

        for (Player player : players) {
            player.setCity(playerCityMap.get(player.getId()));
        }

        playerRepository.saveAll(players);
        log.info("Синхронизация городов завершена. Обновлено игроков: {}", players.size());
    }
}