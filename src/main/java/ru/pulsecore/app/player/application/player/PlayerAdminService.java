package ru.pulsecore.app.player.application.player;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.client.TournamentClient;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.player.infrastructure.persistence.repository.projection.PlayerDataProjection;
import ru.pulsecore.app.player.infrastructure.session.SessionService;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerAdminService {

    private final PlayerRepository playerRepository;
    private final TournamentClient tournamentClient;
    private final RedisIndexedSessionRepository sessionRepository;
    private final SessionService sessionService;
    private final PlayerCommandService playerCommandService;

    @Transactional
    public MessageResponse deletePlayer(UUID playerId) {
        tournamentClient.deleteByPlayerId(playerId);
        String principalName = playerId.toString();
        sessionRepository.findByPrincipalName(principalName)
                .forEach((sessionId, session) -> sessionRepository.deleteById(sessionId));
        playerCommandService.deletePlayer(playerId);
        sessionService.invalidateCurrentSession();
        return new MessageResponse("Аккаунт удалён");
    }


    public List<PlayerData> getPlayers() {
        return playerRepository.findByVerifiedTrueAndIsBlockedFalse()
                .stream()
                .map(PlayerDataProjection::toPlayerData)
                .toList();
    }
}