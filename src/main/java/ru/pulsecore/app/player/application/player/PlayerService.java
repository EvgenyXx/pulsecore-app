package ru.pulsecore.app.player.application.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.player.api.dto.response.PlayerResponse;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.player.infrastructure.exception.PlayerNotFoundException;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final NameNormalizer nameNormalizer;


    public List<PlayerData> findPlayerByIds(Set<UUID> playerIds) {
        return playerRepository.findProjectionsByIds(playerIds)
                .stream()
                .map(p->
                        new PlayerData(p.getId(),p.getName()
                                ,p.getName(),p.getPrimaryLeague(),
                                p.getPushEnabled(),p.getNotificationsEnabled()))
                .toList();
    }

    public Player getById(UUID id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id.toString()));
    }

    public boolean existsByEmail(String email) {
        return playerRepository.existsByEmail(email);
    }


    public List<Player> getAll() {
        return playerRepository.findAll();
    }

    public Optional<Player> findByEmail(String email) {
        return playerRepository.findByEmail(email);
    }


    public List<PlayerResponse> searchPlayers(String q) {
        String normalizedQuery = nameNormalizer.normalizeForSearch(q);
        return playerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(normalizedQuery, q)
                .stream()
                .map(p -> PlayerResponse.builder()
                        .id(p.getId().toString())
                        .name(p.getName())
                        .email(p.getEmail())
                        .build())
                .toList();
    }


    public Player save(Player player) {
        if (player.getName() != null) {
            player.setName(nameNormalizer.normalize(player.getName()));
        }
        return playerRepository.save(player);
    }


    public Player findById(UUID id) {
        return playerRepository.findById(id).orElse(null);
    }
    public Optional<Player> findByIdOptional(UUID id) {
        try {
            return Optional.of(findById(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}