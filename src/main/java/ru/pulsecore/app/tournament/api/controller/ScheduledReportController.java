package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.player.api.dto.response.ScheduledReportResponse;
import ru.pulsecore.app.player.api.dto.request.CreateScheduledReportRequest;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.application.earnings.report.ScheduledReportFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.util.List;
import java.util.UUID;

@Tag(name = "Tournament", description = "Заказ и управление отчетами")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class ScheduledReportController {

    private final ScheduledReportFacade facade;

    @Operation(summary = "Заказать отчет на почту")
    @PostMapping(TournamentApi.REPORTS)
    public ResponseEntity<MessageResponse> createReport(
            @Valid @RequestBody CreateScheduledReportRequest request,
            @CurrentPlayer PlayerPrincipal player) {
        facade.createReport(player.playerId(), request.dateFrom(), request.dateTo(), request.scheduledAt());
        return ResponseEntity.ok(new MessageResponse("Отчет успешно запланирован"));
    }

    @Operation(summary = "Получить список запланированных отчетов")
    @GetMapping(TournamentApi.REPORTS_PENDING)
    public ResponseEntity<List<ScheduledReportResponse>> getReports(@CurrentPlayer PlayerPrincipal player) {
        return ResponseEntity.ok(facade.getPlayerReports(player.playerId()));
    }

    @Operation(summary = "Отменить запланированный отчет")
    @DeleteMapping(TournamentApi.REPORTS_CANCEL)
    public ResponseEntity<Void> cancelReport(@PathVariable UUID id) {
        facade.deleteByScheduled(id);
        return ResponseEntity.noContent().build();
    }
}