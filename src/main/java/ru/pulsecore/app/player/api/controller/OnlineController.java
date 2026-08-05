package ru.pulsecore.app.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.player.api.OnlineApi;
import ru.pulsecore.app.player.application.player.OnlineService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OnlineController {

    private final OnlineService onlineService;

    @GetMapping(OnlineApi.BASE_PATH)
    public ResponseEntity<Map<String, Long>> getOnline() {
        return ResponseEntity.ok(Map.of("online", onlineService.getOnlineCount()));
    }
}