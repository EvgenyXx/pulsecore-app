package ru.pulsecore.app.modules.tournament_module.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player_modeles.infrastructure.client.TournamentClient;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.repository.ChatMessageRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentPlayerClientIml implements TournamentClient {

    private final ChatMessageRepository chatMessageRepository;


    @Override
    public void deleteByPlayerId(UUID playerId) {
        chatMessageRepository.deleteByPlayerId(playerId);
    }
}
