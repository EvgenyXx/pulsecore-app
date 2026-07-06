
package ru.pulsecore.app.modules.player.api.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.MessageResponse;
import ru.pulsecore.app.modules.player.api.dto.dashboard.ScheduledReportResponse;
import ru.pulsecore.app.modules.player.api.dto.scheduled.CreateScheduledReportRequest;
import ru.pulsecore.app.modules.player.service.scheduled.ScheduledReportFacade;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.util.List;
import java.util.UUID;

@ScheduledReport
@RequiredArgsConstructor
public class ScheduledReportController {

    private final ScheduledReportFacade facade;

    @PostMapping(PlayerApi.REPORTS)
    public ResponseEntity<MessageResponse> createReport(
            @Valid @RequestBody CreateScheduledReportRequest request,
            @CurrentPlayer PlayerPrincipal player) {

        facade.createReport(player.playerId(), request.dateFrom(), request.dateTo(), request.scheduledAt());
        return ResponseEntity.ok(new MessageResponse(PlayerApi.REPORT_CREATED));
    }


    @GetMapping(PlayerApi.REPORTS_PENDING)
    public ResponseEntity<List<ScheduledReportResponse>> getReports(@CurrentPlayer PlayerPrincipal player) {

        return ResponseEntity.ok(facade.getPlayerReports(player.playerId()));
    }


    @DeleteMapping(PlayerApi.REPORTS_CANCEL)
    public ResponseEntity<Void> cancelReport(@PathVariable UUID id) {
        facade.deleteByScheduled(id);
        return ResponseEntity.noContent().build();
    }


}