// modules/admin/controller/PageViewController.java
package ru.pulsecore.app.modules.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.pulsecore.app.modules.admin.AdminApi;
import ru.pulsecore.app.modules.admin.dto.PageViewStats;
import ru.pulsecore.app.modules.admin.dto.PlayerPageViewStats;
import ru.pulsecore.app.modules.admin.service.PageViewStatsService;

import java.util.List;

@AdminController
@RequiredArgsConstructor
public class PageViewController {

    private final PageViewStatsService pageViewStatsService;

    @GetMapping(AdminApi.PAGE_VIEWS_STATS)
    public ResponseEntity<List<PageViewStats>> getStats(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(pageViewStatsService.getStats(days));
    }

    @GetMapping(AdminApi.PAGE_VIEWS_PLAYERS)
    public ResponseEntity<List<PlayerPageViewStats>> getPlayerStats(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(pageViewStatsService.getPlayerStats(days));
    }
}