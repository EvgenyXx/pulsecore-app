package ru.pulsecore.app.modules.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.SubscriptionStatusResponse;
import ru.pulsecore.app.modules.player.service.subscribion.SubscriptionFacade;
import ru.pulsecore.app.modules.shared.security.CurrentPlayer;
import ru.pulsecore.app.modules.shared.security.PlayerPrincipal;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionFacade subscriptionFacade;

    @GetMapping(PlayerApi.SUBSCRIPTION)
    public ResponseEntity<SubscriptionStatusResponse> getSubscription(
            @CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(subscriptionFacade.getSubscription(principal.playerId()));
    }
}