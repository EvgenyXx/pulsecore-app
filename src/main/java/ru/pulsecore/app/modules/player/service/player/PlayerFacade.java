package ru.pulsecore.app.modules.player.service.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.auth.api.dto.ChangePasswordRequest;
import ru.pulsecore.app.modules.auth.api.dto.UpdateProfileRequest;
import ru.pulsecore.app.modules.player.api.dto.MessageResponse;
import ru.pulsecore.app.modules.player.api.dto.subscription.NotificationsStatusResponse;
import ru.pulsecore.app.modules.player.api.dto.player.PlayerProfileResponse;
import ru.pulsecore.app.modules.player.api.dto.player.PlayerResponse;
import ru.pulsecore.app.modules.shared.service.SessionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerFacade {

    private final PlayerProfileService profileService;
    private final PlayerNotificationService notificationService;
    private final PlayerHallsService hallsService;
    private final PlayerService playerService;
    private final SessionService sessionService;

    public PlayerProfileResponse updateProfile(UUID playerId, UpdateProfileRequest request) {
        return profileService.updateProfile(playerId, request);
    }

    public MessageResponse changePassword(UUID playerId, ChangePasswordRequest request) {
        profileService.changePassword(playerId, request);
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
        notificationService.setNotificationsEnabled(playerId, enabled);
        return new MessageResponse(enabled ? "Уведомления включены" : "Уведомления отключены");
    }

    public NotificationsStatusResponse getNotificationsStatus(UUID playerId) {
        return new NotificationsStatusResponse(notificationService.isNotificationsEnabled(playerId));
    }

    public void saveSelectedHalls(UUID playerId, String halls) {
        hallsService.saveSelectedHalls(playerId, halls);
    }

    public String getSelectedHalls(UUID playerId) {
        return hallsService.getSelectedHalls(playerId);
    }
}