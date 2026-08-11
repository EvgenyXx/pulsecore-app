package ru.pulsecore.app.player.application.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.NotificationsStatusResponse;
import ru.pulsecore.app.player.api.dto.response.PlayerProfileResponse;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileFacade {


    private final PasswordService passwordService;
    private final UpdatePlayerService updatePlayerService;
    private final PlayerNotificationService playerNotificationService;
    private final ThemeService themeService;
    private final QrCodeService qrCodeService;

    public void verifyPassword(UUID id, String rawPassword) {
        passwordService.verifyPassword(id, rawPassword);
    }

    public MessageResponse changePassword(UUID playerId, String oldPassword,String newPassword) {
        passwordService.changePassword(playerId, oldPassword, newPassword);
        return new MessageResponse("Пароль успешно изменен");
    }

    public PlayerProfileResponse updateProfile(UUID id, String email) {
        return  updatePlayerService.updateProfile(id, email);
    }

    public MessageResponse toggleNotifications(UUID playerId, boolean enabled) {
        playerNotificationService.setNotificationsEnabled(playerId, enabled);
        return new MessageResponse(enabled ? "Уведомления включены" : "Уведомления отключены");
    }

    public NotificationsStatusResponse getNotificationsStatus(UUID playerId) {
        return new NotificationsStatusResponse(playerNotificationService.isNotificationsEnabled(playerId));
    }

    public void setTheme(String playerId, String theme){
            themeService.setTheme(UUID.fromString(playerId), theme);//??
    }

    //todo обрабоать ошибку в сервисе
    public byte[] generateQrCode() throws Exception {
        return qrCodeService.generateQrCode();
    }



}
