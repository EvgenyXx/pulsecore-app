package ru.pulsecore.app.player.api.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.admin.PlayerCreateAdminService;
import ru.pulsecore.app.shared.api.player.CreatePlayerRequest;
import ru.pulsecore.app.shared.api.player.PlayerAdminManagementClient;




@Component
@RequiredArgsConstructor
public class PlayerAdminManagementClientImpl implements PlayerAdminManagementClient {

    private final PlayerCreateAdminService playerCreateAdminService;

    @Override
    public void createPlayer(CreatePlayerRequest request) {
        playerCreateAdminService.createPlayer(request);
    }
}
