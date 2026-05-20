package ru.pulsecore.app.modules.player.service.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.auth.api.dto.ChangePasswordRequest;
import ru.pulsecore.app.modules.auth.api.dto.UpdateProfileRequest;
import ru.pulsecore.app.modules.player.api.dto.MessageResponse;
import ru.pulsecore.app.modules.player.api.dto.NotificationsStatusResponse;
import ru.pulsecore.app.modules.player.api.dto.PlayerProfileResponse;
import ru.pulsecore.app.modules.player.api.dto.PlayerResponse;
import ru.pulsecore.app.modules.player.service.SessionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerFacade {

    private final PlayerService playerService;
    private final SessionService sessionService;

    public PlayerProfileResponse updateProfile(UUID playerId, UpdateProfileRequest request) {
        return playerService.updateProfile(playerId, request);
    }

    public MessageResponse changePassword(UUID playerId, ChangePasswordRequest request) {
        playerService.changePassword(playerId, request);
        return new MessageResponse("Пароль изменён");
    }

    public List<PlayerResponse> searchPlayers(String query) {
        return playerService.searchPlayers(query);
    }

    public MessageResponse deleteAccount(UUID playerId) {
        playerService.deletePlayer(playerId);
        sessionService.invalidateCurrentSession();
        return new MessageResponse("Аккаунт удалён");
    }

    public MessageResponse toggleNotifications(UUID playerId, boolean enabled) {
        playerService.setNotificationsEnabled(playerId, enabled);
        return new MessageResponse(enabled ? "Уведомления включены" : "Уведомления отключены");
    }

    public NotificationsStatusResponse getNotificationsStatus(UUID playerId) {
        return new NotificationsStatusResponse(playerService.isNotificationsEnabled(playerId));
    }
}