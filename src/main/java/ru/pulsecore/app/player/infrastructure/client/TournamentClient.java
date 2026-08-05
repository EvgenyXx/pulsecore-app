package ru.pulsecore.app.player.infrastructure.client;

import java.util.UUID;

public interface TournamentClient {

    void deleteByPlayerId(UUID playerId);
}
