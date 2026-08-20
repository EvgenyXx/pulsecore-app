package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.player.application.player.OnlineService;

import java.util.Map;

@Tag(name = "Player", description = "Количество игроков онлайн")
@RestController
@RequiredArgsConstructor
public class OnlineController {

    private final OnlineService onlineService;

    @Operation(summary = "Получить количество игроков онлайн")
    @GetMapping(PlayerApi.ONLINE)
    public ResponseEntity<Map<String, Long>> getOnline() {
        return ResponseEntity.ok(Map.of("online", onlineService.getOnlineCount()));
    }
}