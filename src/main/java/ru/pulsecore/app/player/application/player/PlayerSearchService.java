package ru.pulsecore.app.player.application.player;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.PlayerNotFoundException;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.player.infrastructure.persistence.repository.projection.PlayerDataProjection;
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

    public Optional<Player> findByOauthProviderAndOauthId(String provider, String oauthId) {
        return playerRepository.findByOauthProviderAndOauthId(provider, oauthId);
    }

    public Optional<Player>findByEmail(String email) {
        return playerRepository.findByEmail(email);
    }

    public List<PlayerData> findPlayerByIds(Set<UUID> playerIds) {
        return playerRepository.findProjectionsByIds(playerIds)
                .stream()
                .map(PlayerDataProjection::toPlayerData)
                .toList();
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
