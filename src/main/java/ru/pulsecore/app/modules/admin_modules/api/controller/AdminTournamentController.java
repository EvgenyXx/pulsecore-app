package ru.pulsecore.app.modules.admin_modules.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.modules.admin_modules.api.AdminApi;
import ru.pulsecore.app.modules.player_modeles.api.dto.response.MessageResponse;
import ru.pulsecore.app.modules.tournament_module.api.dto.response.AdminCalculateResponse;
import ru.pulsecore.app.modules.admin_modules.AdminCalculateService;
import ru.pulsecore.app.modules.tournament_module.application.tournament.TournamentResetService;

import java.util.Map;
import java.util.UUID;

@AdminController
@RequiredArgsConstructor
public class AdminTournamentController {

    private final AdminCalculateService adminCalculateService;
    private final TournamentResetService tournamentResetService;

    @PostMapping(AdminApi.TOURNAMENT_CALCULATE)
    public ResponseEntity<AdminCalculateResponse> calculate(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String startDate = request.get("startDate");
        String endDate = request.get("endDate");

        if (name == null || startDate == null || endDate == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(adminCalculateService.calculate(name, startDate, endDate));
    }

    // Удалить все турниры игрока
    @DeleteMapping(AdminApi.PLAYER_TOURNAMENTS)
    public ResponseEntity<MessageResponse> deletePlayerTournaments(@PathVariable UUID id) {
        int deleted = tournamentResetService.deleteAllTournaments(id);
        return ResponseEntity.ok(new MessageResponse("Удалено турниров: " + deleted));
    }

    // Перезагрузить турниры игрока (как при регистрации)
    @PostMapping(AdminApi.PLAYER_TOURNAMENTS_RESYNC)
    public ResponseEntity<MessageResponse> resyncPlayerTournaments(@PathVariable UUID id) {
        tournamentResetService.resyncAll(id);
        return ResponseEntity.ok(new MessageResponse("Загрузка турниров запущена в фоне"));
    }
}