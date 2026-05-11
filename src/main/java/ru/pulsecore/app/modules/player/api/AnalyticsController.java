// AnalyticsController.java
package ru.pulsecore.app.modules.player.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.player.api.dto.AnalyticsResponse;
import ru.pulsecore.app.modules.player.service.AnalyticsService;

import java.util.UUID;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping(PlayerApi.ANALYTICS)
    public ResponseEntity<AnalyticsResponse> getAnalytics(@RequestParam(defaultValue = "30") int days) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = (SecurityUser) auth.getPrincipal();
        return ResponseEntity.ok(analyticsService.getAnalytics(UUID.fromString(user.getPlayerId()), days));
    }
}