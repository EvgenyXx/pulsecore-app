package ru.pulsecore.app.modules.admin_modules.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.admin_modules.api.AdminApi;
import ru.pulsecore.app.modules.player_modeles.api.dto.response.MessageResponse;
import ru.pulsecore.app.modules.player_modeles.application.player.PlayerFacade;

import java.util.UUID;

@AdminController
@RequiredArgsConstructor
public class AdminPlayerController {

    private final PlayerFacade playerFacade;

    @DeleteMapping(AdminApi.DELETE_PLAYER)
    public ResponseEntity<MessageResponse> deletePlayer(@PathVariable UUID id) {
        return ResponseEntity.ok(playerFacade.deleteAccount(id));
    }
}