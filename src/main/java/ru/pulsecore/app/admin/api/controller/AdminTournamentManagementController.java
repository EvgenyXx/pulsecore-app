package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.api.dto.request.UpdateTournamentRequest;
import ru.pulsecore.app.shared.api.tournament.TournamentAdminClient;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Admin Tournaments", description = "Управление турнирами")
@AdminController
@RequiredArgsConstructor
public class AdminTournamentManagementController {

     private final TournamentAdminClient tournamentClient;

    @Operation(summary = "Получить турниры по дате")
    @GetMapping(AdminApi.TOURNAMENTS_BY_DATE)
    public ResponseEntity<List<AdminTournamentResponse>> getByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(tournamentClient.getTournamentsByDate(targetDate));
    }



    @Operation(summary = "Обновить турнир")
    @PatchMapping(AdminApi.TOURNAMENT_UPDATE)
    public ResponseEntity<AdminTournamentResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateTournamentRequest request) {
        return ResponseEntity.ok(tournamentClient.updateTournament(id, request));
    }
}