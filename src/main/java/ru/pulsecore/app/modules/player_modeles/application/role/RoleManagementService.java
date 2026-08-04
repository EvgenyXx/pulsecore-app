package ru.pulsecore.app.modules.player_modeles.application.role;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player_modeles.entity.Player;
import ru.pulsecore.app.modules.player_modeles.entity.Role;
import ru.pulsecore.app.modules.player_modeles.infrastructure.exception.RoleAlreadyGrantedException;
import ru.pulsecore.app.modules.player_modeles.infrastructure.exception.RoleNotFoundException;
import ru.pulsecore.app.modules.player_modeles.infrastructure.exception.RoleNotGrantedException;
import ru.pulsecore.app.modules.player_modeles.application.player.PlayerService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleManagementService {
    //todo переделать  в интрнел и закинуть туда
    private final RoleService roleService;
    private final PlayerService playerService;


    @Transactional
    public void grantRole(UUID playerId, String roleName) {
        Player player = playerService.getById(playerId);
        Role role = roleService.findByName(roleName);
        if (role == null) throw new RoleNotFoundException(roleName);
        if (player.getRoles().contains(role)) throw new RoleAlreadyGrantedException(roleName);
        player.getRoles().add(role);
        playerService.save(player);
        log.info("✅ Роль {} выдана игроку {}", roleName, player.getEmail());
    }

    @Transactional
    public void revokeRole(UUID playerId, String roleName) {
        Player player = playerService.getById(playerId);
        boolean removed = player.getRoles().removeIf(r -> r.getName().equals(roleName));
        if (!removed) throw new RoleNotGrantedException(roleName);
        playerService.save(player);
        log.info("❌ Роль {} отозвана у игрока {}", roleName, player.getEmail());
    }

    public List<String> getRoleNames(UUID playerId) {
        Player player = playerService.getById(playerId);
        return player.getRoles().stream()
                .map(Role::getName)
                .toList();
    }
}
