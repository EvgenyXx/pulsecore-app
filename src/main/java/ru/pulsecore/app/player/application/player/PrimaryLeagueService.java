package ru.pulsecore.app.player.application.player;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.client.TournamentClient;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class PrimaryLeagueService {

    private final PlayerRepository playerRepository;
    private final TournamentClient tournamentClient;


    @Transactional
    public void updatePrimaryLeague() {
        List<Player> players = playerRepository.findAll();
        Set<UUID> playerIds = players.stream()
                .map(Player::getId)
                .collect(Collectors.toSet());

        Map<UUID, String> playerLeagues = tournamentClient.getLeagues(playerIds)
                .stream()
                .collect(Collectors.toMap(PriorityLeagueResponse::playerId, PriorityLeagueResponse::league));

        List<Player> save = new ArrayList<>();

        for (Player player : players) {
            String league = playerLeagues.get(player.getId());
            if (league != null && !league.equals(player.getPrimaryLeague())) {
                player.setPrimaryLeague(league);
                save.add(player);
            }
        }
        playerRepository.saveAll(save);


        if (!save.isEmpty()) {
            playerRepository.saveAll(save);
            log.info("Обновлена основная лига для {} игроков", save.size());
        } else {
            log.info("Все основные лиги актуальны");
        }
    }
}
