package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.api.dto.response.SumResponse;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.application.earnings.sum.SumFacade;

import java.time.LocalDate;
import java.util.Map;

@Tag(name = "Tournament", description = "Сумма за период и редактирование результатов")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class SumController {

    private final SumFacade sumFacade;

    @Operation(summary = "Получить сумму за период")
    @GetMapping(TournamentApi.SUM)
    public ResponseEntity<SumResponse> getSumById(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(sumFacade.getSum(principal.playerId(), start, end, page, size));
    }

    @Operation(summary = "Обновить результат турнира")
    @PutMapping(TournamentApi.UPDATE_RESULT)
    public ResponseEntity<Map<String, String>> updateResult(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        sumFacade.updateResult(
                id,
                body.get(TournamentApi.PARAM_AMOUNT),
                body.get(TournamentApi.PARAM_BONUS));
        return ResponseEntity.ok(Map.of(TournamentApi.RESP_MESSAGE, TournamentApi.RESP_OK));
    }
}