package ru.pulsecore.app.modules.lineup.api;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.service.LineupFacade;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(LineupApi.BASE_PATH)
@RequiredArgsConstructor
public class LineupController {

    private final LineupFacade lineupFacade;

    @GetMapping(LineupApi.ALL)
    public ResponseEntity<Map<String, List<Lineup>>> getAll(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(lineupFacade.getAllGroupedByHall(date));
    }

    @GetMapping(LineupApi.MY)
    public ResponseEntity<Map<String, List<Lineup>>> getMy(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(lineupFacade.getMyGroupedByHall(principal.playerId(), date));
    }
}