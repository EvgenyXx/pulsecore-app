package ru.pulsecore.app.notification.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.notification.api.PushApi;
import ru.pulsecore.app.notification.api.dto.PushSubscriptionRequest;
import ru.pulsecore.app.notification.application.PushFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.util.Map;

@Tag(name = "Push", description = "Push-уведомления")
@RestController
@RequestMapping(PushApi.BASE_PATH)
@RequiredArgsConstructor
public class PushController {

    private final PushFacade pushFacade;

    @Operation(summary = "Статус подписки на push")
    @GetMapping(PushApi.STATUS)
    public ResponseEntity<Map<String, Boolean>> status(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(Map.of("active", pushFacade.isSubscribed(principal.playerId())));
    }

    @Operation(summary = "Получить VAPID публичный ключ")
    @GetMapping(PushApi.VAPID_PUBLIC_KEY)
    public ResponseEntity<String> getVapidPublicKey() {
        return ResponseEntity.ok(pushFacade.getVapidPublicKey());
    }

    @Operation(summary = "Подписаться на push-уведомления")
    @PostMapping(PushApi.SUBSCRIBE)
    public ResponseEntity<Map<String, String>> subscribe(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestBody PushSubscriptionRequest request) {
        pushFacade.subscribe(principal.playerId(), request);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @Operation(summary = "Отписаться от push-уведомлений")
    @PostMapping(PushApi.UNSUBSCRIBE)
    public ResponseEntity<Map<String, String>> unsubscribe(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestBody Map<String, String> request) {
        pushFacade.unsubscribe(principal.playerId(), request.get("endpoint"));
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @Operation(summary = "Проверить включены ли push-уведомления")
    @GetMapping(PushApi.PUSH_STATUS)
    public ResponseEntity<Map<String, Boolean>> pushStatus(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(Map.of("pushEnabled", pushFacade.isPushEnabled(principal.playerId())));
    }

    @Operation(summary = "Включить/выключить push-уведомления")
    @PostMapping(PushApi.TOGGLE)
    public ResponseEntity<Map<String, Boolean>> togglePush(@CurrentPlayer PlayerPrincipal principal) {
        boolean newState = pushFacade.togglePushEnabled(principal.playerId());
        return ResponseEntity.ok(Map.of("pushEnabled", newState));
    }
}