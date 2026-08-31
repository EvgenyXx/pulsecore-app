package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.api.dto.request.UpdateTournamentRequest;

import ru.pulsecore.app.admin.application.TournamentAdminService;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Admin Tournaments", description = "Управление турнирами")
@AdminController
@RequiredArgsConstructor
public class AdminTournamentManagementController {

    private final TournamentAdminService tournamentAdminService;

    @Operation(summary = "Получить турниры по дате")
    @GetMapping(AdminApi.TOURNAMENTS_BY_DATE)
    public ResponseEntity<List<AdminTournamentResponse>> getByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(tournamentAdminService.getByDate(targetDate));
    }

    @Operation(summary = "Получить турнир по ID")
    @GetMapping(AdminApi.TOURNAMENT_BY_ID)
    public ResponseEntity<AdminTournamentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentAdminService.getById(id));
    }

    @Operation(summary = "Обновить турнир")
    @PatchMapping(AdminApi.TOURNAMENT_UPDATE)
    public ResponseEntity<AdminTournamentResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateTournamentRequest request) {
        return ResponseEntity.ok(tournamentAdminService.update(id, request));
    }
}