package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.response.PlayerCompareDto;
import ru.pulsecore.app.tournament.api.dto.response.PlayerMatchStatsDto;
import ru.pulsecore.app.tournament.application.compare.CompareService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Compare", description = "Сравнение игроков H2H")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class CompareController {

    private final CompareService compareService;

    @Operation(
            summary = "Получить список игроков для сравнения",
            description = "Возвращает список игроков с их статистикой за указанный период. " +
                    "Если даты не указаны — возвращает за всё время."
    )
    @GetMapping(TournamentApi.COMPARE_PLAYERS)
    public ResponseEntity<List<PlayerCompareDto>> getPlayersForCompare(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(compareService.getPlayersForCompare(start, end));
    }

    @GetMapping(TournamentApi.COMPARE_MATCH_STATS)
    public List<PlayerMatchStatsDto> getPlayersMatchStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return compareService.getPlayersMatchStats(start, end);
    }
}