package ru.pulsecore.app.player.application.role;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Role;
import ru.pulsecore.app.player.infrastructure.exception.RoleAlreadyGrantedException;
import ru.pulsecore.app.player.infrastructure.exception.RoleNotFoundException;
import ru.pulsecore.app.player.infrastructure.exception.RoleNotGrantedException;
import ru.pulsecore.app.player.application.player.PlayerCommandService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleManagementService {

    private final RoleService roleService;
    private final PlayerSearchService playerSearchService;
    private final PlayerCommandService commandService;



    @Transactional
    public void grantRole(UUID playerId, String roleName) {
        Player player = playerSearchService.getById(playerId);
        Role role = roleService.findByName(roleName);
        if (role == null) throw new RoleNotFoundException(roleName);
        if (player.getRoles().contains(role)) throw new RoleAlreadyGrantedException(roleName);
        player.getRoles().add(role);
        commandService.save(player);
        log.info("✅ Роль {} выдана игроку {}", roleName, player.getEmail());
    }

    @Transactional
    public void revokeRole(UUID playerId, String roleName) {
        Player player = playerSearchService.getById(playerId);
        boolean removed = player.getRoles().removeIf(r -> r.getName().equals(roleName));
        if (!removed) throw new RoleNotGrantedException(roleName);
        commandService.save(player);
        log.info("❌ Роль {} отозвана у игрока {}", roleName, player.getEmail());
    }

    public List<String> getRoleNames(UUID playerId) {
        Player player = playerSearchService.getById(playerId);
        return player.getRoles().stream()
                .map(Role::getName)
                .toList();
    }
}
