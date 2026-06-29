package ru.pulsecore.app.modules.admin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.pulsecore.app.modules.admin.api.AdminApi;
import ru.pulsecore.app.modules.player.api.dto.MessageResponse;
import ru.pulsecore.app.modules.player.service.role.RoleManagementService;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@AdminController
public class AdminRoleController {

    private final RoleManagementService roleManagementService;

    @PostMapping(AdminApi.ROLES_GRANT)
    public ResponseEntity<MessageResponse> grantRole(@PathVariable UUID id, @RequestParam String role) {
        roleManagementService.grantRole(id, role);
        return ResponseEntity.ok(new MessageResponse("Роль " + role + " выдана"));
    }

    @DeleteMapping(AdminApi.ROLES_REVOKE)
    public ResponseEntity<MessageResponse> revokeRole(@PathVariable UUID id, @RequestParam String role) {
        roleManagementService.revokeRole(id, role);
        return ResponseEntity.ok(new MessageResponse("Роль " + role + " отозвана"));
    }

    @GetMapping(AdminApi.ROLES)
    public ResponseEntity<List<String>> getRoles(@PathVariable UUID id) {
        return ResponseEntity.ok(roleManagementService.getRoleNames(id));
    }
}