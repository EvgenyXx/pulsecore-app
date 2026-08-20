package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import java.util.List;
import java.util.UUID;

@Tag(name = "Admin", description = "Управление ролями игроков")
@RequiredArgsConstructor
@AdminController
public class AdminRoleController {

    private final PlayerClient playerClient;

    @Operation(summary = "Выдать роль игроку")
    @PostMapping(AdminApi.ROLES_GRANT)
    public ResponseEntity<MessageResponse> grantRole(@PathVariable UUID id, @RequestParam String role) {
        return ResponseEntity.ok(playerClient.grandRole(id, role));
    }

    @Operation(summary = "Отозвать роль у игрока")
    @DeleteMapping(AdminApi.ROLES_REVOKE)
    public ResponseEntity<MessageResponse> revokeRole(@PathVariable UUID id, @RequestParam String role) {
        return ResponseEntity.ok(playerClient.revokeRole(id, role));
    }

    @Operation(summary = "Получить роли игрока")
    @GetMapping(AdminApi.ROLES)
    public ResponseEntity<List<String>> getRoles(@PathVariable UUID id) {
        return ResponseEntity.ok(playerClient.getRoles(id));
    }
}