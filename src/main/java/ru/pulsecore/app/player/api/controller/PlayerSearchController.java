package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.shared.dto.response.PlayerData;

@Tag(name = "Player", description = "Поиск игроков в системе")
@RestController
@RequiredArgsConstructor
@RequestMapping(PlayerApi.BASE_PATH)
public class PlayerSearchController {

    private final PlayerSearchService playerSearchService;

    @Operation(
            summary = "Поиск зарегистрированных игроков",
            description = "Ищет игроков по имени только среди зарегистрированных в системе. С пагинацией."
    )
    @GetMapping(PlayerApi.SEARCH)
    public ResponseEntity<Page<PlayerData>> searchPlayers(
            @RequestParam("q") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(playerSearchService.searchRegisteredPlayers(query, page, size));
    }
}