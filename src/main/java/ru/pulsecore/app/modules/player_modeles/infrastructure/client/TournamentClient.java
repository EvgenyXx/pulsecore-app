package ru.pulsecore.app.modules.player_modeles.infrastructure.client;

import java.util.UUID;

public interface TournamentClient {

    void deleteByPlayerId(UUID playerId);
}
