package ru.pulsecore.app.modules.admin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.admin.api.AdminApi;
import ru.pulsecore.app.modules.admin.infrastructure.clinet.PlayerClient;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@AdminController
public class AdminRoleController {

    private final PlayerClient   playerClient;

    @PostMapping(AdminApi.ROLES_GRANT)
    public ResponseEntity<MessageResponse> grantRole(@PathVariable UUID id, @RequestParam String role) {

        return ResponseEntity.ok(playerClient.grandRole(id, role));
    }

    @DeleteMapping(AdminApi.ROLES_REVOKE)
    public ResponseEntity<MessageResponse> revokeRole(@PathVariable UUID id, @RequestParam String role) {

        return ResponseEntity.ok(playerClient.revokeRole(id, role));
    }

    @GetMapping(AdminApi.ROLES)
    public ResponseEntity<List<String>> getRoles(@PathVariable UUID id) {
        return ResponseEntity.ok(playerClient.getRoles(id));
    }
}