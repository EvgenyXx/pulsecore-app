package ru.pulsecore.app.modules.push.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.push.service.PushFacade;
import ru.pulsecore.app.modules.push.api.dto.PushSubscriptionRequest;
import ru.pulsecore.app.modules.push.api.PushApi;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.util.Map;

@RestController
@RequestMapping(PushApi.BASE_PATH)
@RequiredArgsConstructor
public class PushController {

    private final PushFacade pushFacade;

    @GetMapping(PushApi.STATUS)
    public ResponseEntity<Map<String, Boolean>> status(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(Map.of("active", pushFacade.isSubscribed(principal.playerId())));
    }


    @GetMapping(PushApi.VAPID_PUBLIC_KEY)
    public ResponseEntity<String> getVapidPublicKey() {
        return ResponseEntity.ok(pushFacade.getVapidPublicKey());
    }

    @PostMapping(PushApi.SUBSCRIBE)
    public ResponseEntity<Map<String, String>> subscribe(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestBody PushSubscriptionRequest request) {
        pushFacade.subscribe(principal.playerId(), request);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping(PushApi.UNSUBSCRIBE)
    public ResponseEntity<Map<String, String>> unsubscribe(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestBody Map<String, String> request) {
        pushFacade.unsubscribe(principal.playerId(), request.get("endpoint"));
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}