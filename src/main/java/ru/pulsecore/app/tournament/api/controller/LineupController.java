package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.response.LineupDto;
import ru.pulsecore.app.tournament.application.lineup.LineupFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Tournament", description = "Составы и расписание турниров")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class LineupController {

    private final LineupFacade lineupFacade;

    @Operation(summary = "Все составы на дату по всем залам")
    @GetMapping(TournamentApi.ALL)
    public ResponseEntity<Map<String, List<LineupDto>>> getAll(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(lineupFacade.getAllGroupedByHall(date));
    }

    @Operation(summary = "Составы по выбранным залам игрока на дату")
    @GetMapping(TournamentApi.MY)
    public ResponseEntity<Map<String, List<LineupDto>>> getMy(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(lineupFacade.getMyGroupedByHall(principal.playerId(), date));
    }
}