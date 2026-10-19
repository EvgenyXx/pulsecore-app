package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.request.SalaryH2HRequest;
import ru.pulsecore.app.tournament.api.dto.response.PlayerH2HMatchDto;
import ru.pulsecore.app.tournament.api.dto.response.PlayerH2HResponseDto;
import ru.pulsecore.app.tournament.api.dto.response.SalaryH2HResponseDto;
import ru.pulsecore.app.tournament.application.compare.CompareService;
import ru.pulsecore.app.tournament.application.compare.SalaryH2HService;

import java.time.LocalDate;

@Tag(name = "Compare", description = "Сравнение игроков H2H")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class CompareController {

    private final CompareService compareService;
    private final SalaryH2HService salaryH2HService;

    @Operation(summary = "Сводка H2H: общее число матчей и победы каждого")
    @GetMapping(TournamentApi.COMPARE_H2H)
    public ResponseEntity<PlayerH2HResponseDto> getH2H(
            @RequestParam String player1Name,
            @RequestParam String player2Name,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(compareService.getH2H(player1Name, player2Name, start, end));
    }

    @Operation(summary = "Список матчей H2H с пагинацией")
    @GetMapping(TournamentApi.COMPARE_H2H_MATCHES)
    public ResponseEntity<Page<PlayerH2HMatchDto>> getH2HMatches(
            @RequestParam String player1Name,
            @RequestParam String player2Name,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(
                compareService.getH2HMatches(player1Name, player2Name, start, end, page, size)
        );
    }

    @Operation(
            summary = "Поиск игроков для сравнения",
            description = "Ищет игроков по имени среди всех, кто играл матчи. " +
                    "Включая незарегистрированных. С пагинацией."
    )
    @GetMapping(TournamentApi.COMPARE_PLAYERS_SEARCH)
    public ResponseEntity<Page<String>> searchPlayers(
            @RequestParam("q") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(compareService.searchPlayersPage(query, page, size));
    }

    @Operation(summary = "Сравнение игроков по зарплате (H2H с TOPSIS)")
    @GetMapping(TournamentApi.COMPARE_SALARY_H2H)
    public ResponseEntity<SalaryH2HResponseDto> getSalaryH2H(@Valid SalaryH2HRequest request) {
        return ResponseEntity.ok(salaryH2HService.getSalaryH2H(
                request.player1Name(),
                request.player2Name(),
                request.start(),
                request.end()
        ));
    }
}