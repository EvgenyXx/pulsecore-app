package ru.pulsecore.app.modules.admin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.pulsecore.app.modules.admin.api.AdminApi;
import ru.pulsecore.app.modules.shared.dto.PageViewStats;
import ru.pulsecore.app.modules.shared.dto.PlayerPageViewStats;
import ru.pulsecore.app.modules.admin.infrastructure.clinet.PlayerClient;


import java.util.List;

@AdminController
@RequiredArgsConstructor
public class PageViewController {

    private final PlayerClient playerClient;

    @GetMapping(AdminApi.PAGE_VIEWS_STATS)
    public ResponseEntity<List<PageViewStats>> getStats(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(playerClient.getStats(days));
    }

    @GetMapping(AdminApi.PAGE_VIEWS_PLAYERS)
    public ResponseEntity<List<PlayerPageViewStats>> getPlayerStats(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(playerClient.getPlayerStats(days));
    }
}