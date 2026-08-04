package ru.pulsecore.app.modules.admin_modules.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.admin_modules.api.AdminApi;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;
import ru.pulsecore.app.modules.shared.dto.SubscriptionStatusResponse;
import ru.pulsecore.app.modules.player_modeles.entity.Subscription;
import ru.pulsecore.app.modules.player_modeles.application.subscription.SubscriptionService;

import java.util.UUID;

@AdminController
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping(AdminApi.SUBSCRIBE)
    public ResponseEntity<MessageResponse> subscribe(@PathVariable UUID id, @RequestParam(defaultValue = "30") int days) {
        subscriptionService.activate(id, days);
        return ResponseEntity.ok(new MessageResponse("Подписка активирована на " + days + " дней"));
    }

    @DeleteMapping(AdminApi.UNSUBSCRIBE)
    public ResponseEntity<MessageResponse> unsubscribe(@PathVariable UUID id) {
        subscriptionService.deactivate(id);
        return ResponseEntity.ok(new MessageResponse("Подписка отключена"));
    }

    // НОВЫЙ МЕТОД
    @GetMapping(AdminApi.PLAYER_SUBSCRIPTION)
    public ResponseEntity<SubscriptionStatusResponse> getSubscription(@PathVariable UUID id) {
        Subscription sub = subscriptionService.getByPlayerId(id);
        if (sub == null) return ResponseEntity.ok(new SubscriptionStatusResponse(false, null, null));
        return ResponseEntity.ok(SubscriptionStatusResponse.builder()
                .active(sub.isActiveNow())
                .expiresAt(sub.getExpiresAt() != null ? sub.getExpiresAt().toString() : null)
                .startedAt(sub.getStartedAt() != null ? sub.getStartedAt().toString() : null)
                .build());
    }
}