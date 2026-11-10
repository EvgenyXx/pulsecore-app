package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.shared.api.player.CreatePlayerRequest;
import ru.pulsecore.app.shared.api.player.PlayerAdminManagementClient;
import ru.pulsecore.app.shared.dto.response.MessageResponse;


@Tag(name = "Admin", description = "Создание игроков")
@AdminController
@RequiredArgsConstructor
public class AdminPlayerRegistrationController {

    private final PlayerAdminManagementClient playerAdminManagementClient;

    @Operation(summary = "Создать нового игрока вручную")
    @PostMapping(AdminApi.CREATE_PLAYER)
    public ResponseEntity<MessageResponse> createPlayer(@RequestBody CreatePlayerRequest request) {
        playerAdminManagementClient.createPlayer(request);
        return ResponseEntity.ok(new MessageResponse("Игрок успешно создан"));
    }
}