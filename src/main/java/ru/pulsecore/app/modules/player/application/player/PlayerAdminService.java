package ru.pulsecore.app.modules.player.application.player;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.infrastructure.client.TournamentClient;
import ru.pulsecore.app.modules.player.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.modules.player.infrastructure.session.SessionService;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;
import ru.pulsecore.app.modules.shared.dto.PlayerData;

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

    public MessageResponse deletePlayer(UUID playerId) {
        tournamentClient.deleteByPlayerId(playerId);
        String principalName = playerId.toString();
        sessionRepository.findByPrincipalName(principalName)
                .forEach((sessionId, session) -> sessionRepository.deleteById(sessionId));
        playerRepository.deleteById(playerId);
        sessionService.invalidateCurrentSession();
        return new MessageResponse("Аккаунт удалён");
    }


    public List<PlayerData> getPlayers() {
        return playerRepository.findByVerifiedTrueAndIsBlockedFalse()
                .stream()
                .map(p ->
                        new PlayerData(p.getId(), p.getName(), p.getEmail()))
                .toList();
    }
}