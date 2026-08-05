package ru.pulsecore.app.modules.admin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.admin.api.AdminApi;
import ru.pulsecore.app.modules.admin.infrastructure.clinet.PlayerClient;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;


import java.util.UUID;

@AdminController
@RequiredArgsConstructor
public class AdminPlayerController {

    private final PlayerClient playerClient;

    @DeleteMapping(AdminApi.DELETE_PLAYER)
    public ResponseEntity<MessageResponse> deletePlayer(@PathVariable UUID id) {
        return ResponseEntity.ok(playerClient.deletePlayer(id));
    }
}