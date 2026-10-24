package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.response.LineupDto;
import ru.pulsecore.app.tournament.application.lineup.LineupService;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Tournament", description = "Составы и расписание турниров")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class LineupController {

    private final LineupService lineupService;

    @Operation(summary = "Все залы на дату")
    @GetMapping(TournamentApi.ALL)
    public ResponseEntity<List<String>> getAll(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(lineupService.getAvailableHalls(date));
    }

    @Operation(summary = "Составы по выбранным залам игрока на дату (с пагинацией)")
    @GetMapping(TournamentApi.MY)
    public ResponseEntity<Page<LineupDto>> getMy(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {

        return ResponseEntity.ok(lineupService.getMyPaged(principal.playerId(), date, page, size));
    }
}