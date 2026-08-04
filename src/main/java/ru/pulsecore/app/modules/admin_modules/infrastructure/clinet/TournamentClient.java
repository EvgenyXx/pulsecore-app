package ru.pulsecore.app.modules.admin_modules.infrastructure.clinet;

import ru.pulsecore.app.modules.shared.dto.AdminCalculateResponse;

import java.util.UUID;

public interface TournamentClient {

    AdminCalculateResponse calculate(String name, String startDate, String endDate);
    int deleteAllTournaments(UUID playerId);
    void resyncAll(UUID playerId);
}
