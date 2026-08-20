package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.shared.dto.response.PageViewStats;
import ru.pulsecore.app.shared.dto.response.PlayerPageViewStats;
import ru.pulsecore.app.admin.client.PlayerClient;

import java.util.List;

@Tag(name = "Admin", description = "Статистика посещений")
@AdminController
@RequiredArgsConstructor
public class PageViewController {

    private final PlayerClient playerClient;

    @Operation(summary = "Статистика по страницам")
    @GetMapping(AdminApi.PAGE_VIEWS_STATS)
    public ResponseEntity<List<PageViewStats>> getStats(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(playerClient.getStats(days));
    }

    @Operation(summary = "Статистика по игрокам")
    @GetMapping(AdminApi.PAGE_VIEWS_PLAYERS)
    public ResponseEntity<List<PlayerPageViewStats>> getPlayerStats(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(playerClient.getPlayerStats(days));
    }
}