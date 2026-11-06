package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.shared.api.tournament.SchedulerAdminClient;

import java.util.Map;

@Slf4j
@AdminController
@Tag(name = "Admin Scheduler", description = "Управление шедулерами")
@RequiredArgsConstructor
public class AdminSchedulerController {

    private final SchedulerAdminClient schedulerAdminClient;

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
        log.info("ЗАПУСК ИЗ АДМИНКИ: проверка старта турниров");
        schedulerAdminClient.checkStart();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обработать завершенные")
    @PostMapping(AdminApi.SCHEDULER_PROCESS_FINISHED)
    public ResponseEntity<Void> processFinishedTournaments() {
        log.info("ЗАПУСК ИЗ АДМИНКИ: обработка завершенных турниров");
        schedulerAdminClient.processFinishedTournaments();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Проверить новые")
    @PostMapping(AdminApi.SCHEDULER_CHECK_NEW)
    public ResponseEntity<Void> checkNewTournaments() {
        log.info("ЗАПУСК ИЗ АДМИНКИ: проверка новых турниров");
        schedulerAdminClient.checkNewTournaments();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Проверить измененные")
    @PostMapping(AdminApi.SCHEDULER_CHECK_CHANGED)
    public ResponseEntity<Void> checkChangedTournament() {
        log.info("ЗАПУСК ИЗ АДМИНКИ: проверка измененных турниров");
        schedulerAdminClient.checkChangedTournament();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Проверить отмененные")
    @PostMapping(AdminApi.SCHEDULER_CHECK_CANCELED)
    public ResponseEntity<Void> checkCanceled() {
        log.info("ЗАПУСК ИЗ АДМИНКИ: проверка отмененных турниров");
        schedulerAdminClient.checkCanceled();
        return ResponseEntity.ok().build();
    }

}