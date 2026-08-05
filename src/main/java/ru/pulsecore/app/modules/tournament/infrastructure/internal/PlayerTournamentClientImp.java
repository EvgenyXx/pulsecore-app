package ru.pulsecore.app.modules.tournament.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.infrastructure.client.TournamentClient;
import ru.pulsecore.app.modules.tournament.infrastructure.persistence.repository.ChatMessageRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerTournamentClientImp implements TournamentClient {

    private final ChatMessageRepository chatMessageRepository;


    @Override
    public void deleteByPlayerId(UUID playerId) {
        chatMessageRepository.deleteByPlayerId(playerId);
    }
}
