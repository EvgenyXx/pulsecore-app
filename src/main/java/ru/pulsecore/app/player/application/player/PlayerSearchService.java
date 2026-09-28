package ru.pulsecore.app.player.application.player;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.application.mapper.PlayerMapper;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.PlayerNotFoundException;
import ru.pulsecore.app.player.infrastructure.repository.PlayerRepository;
import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис для внутреннего поиска пользователя
 */
@Service
@RequiredArgsConstructor
public class PlayerSearchService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public List<PlayerData> getPlayersIds(Set<UUID> playerIds) {
        return playerRepository.findProjectionsByIds(playerIds)
                .stream()
                .map(playerMapper::toData)
                .toList();
    }


    public List<PlayerData> getAllActivePlayers() {
        return playerRepository.findActivePlayers()
                .stream()
                .map(playerMapper::toData)
                .toList();
    }


    public PlayerData findByName(String fullName) {
        return playerRepository.findByNameIgnoreCase(fullName)
                .map(playerMapper::toData)
                .orElseThrow(() -> new PlayerNotFoundException(fullName));
    }

    public List<PlayerData> searchByName(String query) {
        return playerRepository.searchByName(query)
                .stream()
                .map(playerMapper::toData)
                .toList();
    }


    public PlayerData getPlayerById(UUID playerId) {
        return playerRepository.findProjectionById(playerId)
                .map(playerMapper::toData)
                .orElseThrow(() -> new PlayerNotFoundException(playerId.toString()));
    }

    public List<PlayerData> getAll() {
        return playerRepository.findAllPlayers()
                .stream().map(playerMapper::toData)
                .toList();
    }

    public Page<PlayerData> searchByNamePage(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return playerRepository.searchByName(name, pageable)
                .map(playerMapper::toData);
    }

    public Optional<Player> findByOauthProviderAndOauthId(String provider, String oauthId) {
        return playerRepository.findByOauthProviderAndOauthId(provider, oauthId);
    }

    public Optional<Player> findByEmail(String email) {
        return playerRepository.findByEmail(email);
    }


    public Player getById(UUID id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id.toString()));
    }

    public boolean existsByEmail(String email) {
        return playerRepository.existsByEmail(email);
    }

    public boolean existsByName(String name) {
        return playerRepository.existsByNameIgnoreCase(name);
    }
}
