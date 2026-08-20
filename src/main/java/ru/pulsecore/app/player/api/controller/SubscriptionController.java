package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.shared.dto.response.SubscriptionStatusResponse;
import ru.pulsecore.app.player.application.subscription.SubscriptionQueryService;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

@Tag(name = "Player", description = "Подписка игрока")
@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionQueryService subscriptionQueryService;

    @Operation(summary = "Получить статус подписки")
    @GetMapping(PlayerApi.SUBSCRIPTION)
    public ResponseEntity<SubscriptionStatusResponse> getSubscription(
            @CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(subscriptionQueryService.getSubscription(principal.playerId()));
    }
}