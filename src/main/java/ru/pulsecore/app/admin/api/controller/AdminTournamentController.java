package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.api.dto.request.AdminCalculateRequest;
import ru.pulsecore.app.admin.api.dto.request.ResyncRequest;
import ru.pulsecore.app.admin.client.TournamentClient;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.shared.dto.response.AdminCalculateResponse;


import java.util.UUID;

@Tag(name = "Admin", description = "Управление турнирами игроков")
@AdminController
@RequiredArgsConstructor
public class AdminTournamentController {

    private final TournamentClient tournamentClient;

    @Operation(summary = "Рассчитать результаты игрока за период")
    @PostMapping(AdminApi.TOURNAMENT_CALCULATE)
    public ResponseEntity<AdminCalculateResponse> calculate(
            @Valid @RequestBody AdminCalculateRequest request) {
        return ResponseEntity.ok(
                tournamentClient.calculate(request.name(), request.startDate(), request.endDate())
        );
    }

    @Operation(summary = "Удалить все турниры игрока")
    @DeleteMapping(AdminApi.PLAYER_TOURNAMENTS)
    public ResponseEntity<MessageResponse> deletePlayerTournaments(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentClient.deleteAllTournaments(id));
    }

    @Operation(summary = "Запустить ресинхронизацию турниров игрока за период")
    @PostMapping(AdminApi.PLAYER_TOURNAMENTS_RESYNC)
    public ResponseEntity<MessageResponse> resyncPlayerTournaments(
            @PathVariable UUID id,
            @RequestBody @Valid ResyncRequest request) {
        return ResponseEntity.ok(tournamentClient.resyncPeriod(id, request.from(),request.to()));
    }
}