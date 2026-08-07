package ru.pulsecore.app.admin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.shared.dto.response.SubscriptionStatusResponse;


import java.util.UUID;

@AdminController
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final PlayerClient playerClient;

    //todo сделать дто request?
    @PostMapping(AdminApi.SUBSCRIBE)
    public ResponseEntity<MessageResponse> subscribe(@PathVariable UUID id,
                                                     @RequestParam(defaultValue = "30") int days) {
       return ResponseEntity.ok(playerClient.activate(id, days));
    }

    @DeleteMapping(AdminApi.UNSUBSCRIBE)
    public ResponseEntity<MessageResponse> unsubscribe(@PathVariable UUID id) {
       return ResponseEntity.ok(playerClient.deactivate(id));
    }


    @GetMapping(AdminApi.PLAYER_SUBSCRIPTION)
    public ResponseEntity<SubscriptionStatusResponse> getSubscription(@PathVariable UUID id) {
       return ResponseEntity.ok(playerClient.getSubscription(id));
    }
}