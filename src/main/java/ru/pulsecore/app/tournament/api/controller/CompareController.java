package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.tournament.api.dto.response.PlayerCompareDto;
import ru.pulsecore.app.tournament.application.compare.CompareService;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PlayerCompareResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Compare", description = "Сравнение игроков")
@RestController
@RequestMapping("/api/tournament/compare")
@RequiredArgsConstructor
public class CompareController {

    private final CompareService compareService;

    @Operation(summary = "Список игроков для сравнения")
    @GetMapping("/players")
    public ResponseEntity<List<PlayerCompareDto>> getPlayersForCompare(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(compareService.getPlayersForCompare(start, end));
    }
}