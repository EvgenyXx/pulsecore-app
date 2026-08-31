package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.application.SchedulerTournamentService;

import java.util.Map;

@AdminController
@Tag(name = "Admin Scheduler", description = "Управление шедулерами")
@RequiredArgsConstructor
public class AdminSchedulerController {

    private final SchedulerTournamentService schedulerTournamentService;

    @Operation(summary = "Поставить планировщик на паузу")
    @PostMapping(AdminApi.SCHEDULER_PAUSE)
    public ResponseEntity<Map<String, Boolean>> pause() {
        schedulerTournamentService.pause();
        return ResponseEntity.ok(Map.of("paused", true));
    }

    @Operation(summary = "Возобновить планировщик")
    @PostMapping(AdminApi.SCHEDULER_RESUME)
    public ResponseEntity<Map<String, Boolean>> resume() {
        schedulerTournamentService.resume();
        return ResponseEntity.ok(Map.of("paused", false));
    }

    @Operation(summary = "Статус планировщика")
    @GetMapping(AdminApi.SCHEDULER_STATUS)
    public ResponseEntity<Map<String, Boolean>> status() {
        return ResponseEntity.ok(Map.of("paused", schedulerTournamentService.isPaused()));
    }
}