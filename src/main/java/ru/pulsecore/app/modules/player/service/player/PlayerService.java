package ru.pulsecore.app.modules.player.service.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.api.dto.player.PlayerResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.shared.exception.PlayerNotFoundException;
import ru.pulsecore.app.modules.shared.service.NameNormalizer;
import ru.pulsecore.app.modules.tournament.persistence.repository.ChatMessageRepository;

import org.springframework.session.data.redis.RedisIndexedSessionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final NameNormalizer nameNormalizer;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisIndexedSessionRepository sessionRepository;

    public Player getById(UUID id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id.toString()));
    }

    public Player findById(UUID id) {
        return playerRepository.findById(id).orElse(null);
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

    @Transactional
    public Player save(Player player) {
        if (player.getName() != null) {
            player.setName(nameNormalizer.normalize(player.getName()));
        }
        return playerRepository.save(player);
    }

    @Transactional
    public void deletePlayer(UUID id) {
        chatMessageRepository.deleteByPlayerId(id);
        String principalName = id.toString();
        sessionRepository.findByPrincipalName(principalName).forEach((sessionId, session) -> {
            sessionRepository.deleteById(sessionId);
        });
        playerRepository.deleteById(id);
    }
}