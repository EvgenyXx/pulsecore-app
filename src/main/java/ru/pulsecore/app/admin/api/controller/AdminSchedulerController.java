package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.shared.api.player.PlayerSchedulerClient;
import ru.pulsecore.app.shared.api.tournament.SchedulerAdminClient;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import java.util.Map;


@AdminController
@Tag(name = "Admin Scheduler", description = "Управление шедулерами")
@RequiredArgsConstructor
public class AdminSchedulerController {

    private final SchedulerAdminClient schedulerAdminClient;
    private final PlayerSchedulerClient playerSchedulerClient;

    @Operation(summary = "Синхронизировать города игроков")
    @PostMapping(AdminApi.PLAYER_CITIES_SYNC)
    public ResponseEntity<MessageResponse> syncUpcomingCities() {
        playerSchedulerClient.syncUpcomingCities();
        return ResponseEntity.ok(new MessageResponse("Синхронизация городов запущена"));
    }

    @Operation(summary = "Поставить планировщик на паузу")
    @PostMapping(AdminApi.SCHEDULER_PAUSE)
    public ResponseEntity<Map<String, Boolean>> pause() {
        schedulerAdminClient.pauseScheduler();
        return ResponseEntity.ok(Map.of("paused", true));
    }

    @Operation(summary = "Возобновить планировщик")
    @PostMapping(AdminApi.SCHEDULER_RESUME)
    public ResponseEntity<Map<String, Boolean>> resume() {
        schedulerAdminClient.resumeScheduler();
        return ResponseEntity.ok(Map.of("paused", false));
    }

    @Operation(summary = "Статус планировщика")
    @GetMapping(AdminApi.SCHEDULER_STATUS)
    public ResponseEntity<Map<String, Boolean>> status() {
        return ResponseEntity.ok(Map.of("paused", schedulerAdminClient.isSchedulerPaused()));
    }

    @Operation(summary = "Проверить старт")
    @PostMapping(AdminApi.SCHEDULER_CHECK_START)
    public ResponseEntity<Void> checkStart() {
        schedulerAdminClient.checkStart();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обработать завершенные")
    @PostMapping(AdminApi.SCHEDULER_PROCESS_FINISHED)
    public ResponseEntity<Void> processFinishedTournaments() {
        schedulerAdminClient.processFinishedTournaments();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Проверить новые")
    @PostMapping(AdminApi.SCHEDULER_CHECK_NEW)
    public ResponseEntity<Void> checkNewTournaments() {
        schedulerAdminClient.checkNewTournaments();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Проверить измененные")
    @PostMapping(AdminApi.SCHEDULER_CHECK_CHANGED)
    public ResponseEntity<Void> checkChangedTournament() {
        schedulerAdminClient.checkChangedTournament();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Проверить отмененные")
    @PostMapping(AdminApi.SCHEDULER_CHECK_CANCELED)
    public ResponseEntity<Void> checkCanceled() {
        schedulerAdminClient.checkCanceled();
        return ResponseEntity.ok().build();
    }

}