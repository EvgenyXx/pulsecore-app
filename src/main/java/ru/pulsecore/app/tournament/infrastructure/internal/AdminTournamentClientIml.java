package ru.pulsecore.app.tournament.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.admin.infrastructure.clinet.TournamentClient;
import ru.pulsecore.app.shared.dto.AdminCalculateResponse;
import ru.pulsecore.app.shared.dto.MessageResponse;
import ru.pulsecore.app.tournament.application.admin.AdminCalculateService;
import ru.pulsecore.app.tournament.application.tournament.TournamentResetService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminTournamentClientIml implements TournamentClient {

    private final TournamentResetService tournamentResetService;
    private final AdminCalculateService adminCalculateService;


    @Override
    public AdminCalculateResponse calculate(String name, String startDate, String endDate) {
        return adminCalculateService.calculate(name, startDate, endDate);
    }

    @Override
    public MessageResponse deleteAllTournaments(UUID playerId) {
        int deleted = tournamentResetService.deleteAllTournaments(playerId);
        return new MessageResponse("Удалено турниров: " + deleted);
    }

    @Override
    public MessageResponse resyncAll(UUID playerId) {
        tournamentResetService.resyncAll(playerId);
        return new MessageResponse("Загрузка турниров запущена в фоне");
    }
}
