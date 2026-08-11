package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin", description = "Управление игроками")
@AdminController
@RequiredArgsConstructor
public class AdminPlayerController {

    private final PlayerClient playerClient;

    @Operation(summary = "Удалить игрока")
    @DeleteMapping(AdminApi.DELETE_PLAYER)
    public ResponseEntity<MessageResponse> deletePlayer(@PathVariable UUID id) {
        return ResponseEntity.ok(playerClient.deletePlayer(id));
    }

    @Operation(summary = "Поиск игроков по имени")
    @GetMapping(AdminApi.SEARCH_BY_NAME)
    public ResponseEntity<List<PlayerData>> getPlayersByName(@RequestParam("q") String q) {
        return ResponseEntity.ok(playerClient.searchByName(q));
    }
}