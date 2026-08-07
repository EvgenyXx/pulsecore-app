package ru.pulsecore.app.player.application.profile;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.request.ChangePasswordRequest;
import ru.pulsecore.app.player.application.player.PlayerService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.OldPasswordMismatchException;
import ru.pulsecore.app.player.infrastructure.exception.SamePasswordException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final PlayerService playerService;
    private final PasswordEncoder passwordEncoder;

    public void verifyPassword(UUID id, String rawPassword) {
        Player player = playerService.getById(id);
        if (!passwordEncoder.matches(rawPassword, player.getPassword())) {
            log.warn("Неверный пароль для игрока: {}", player.getEmail());
            throw new OldPasswordMismatchException();
        }
    }

    public void changePassword(UUID id, ChangePasswordRequest request) {
        Player player = playerService.getById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), player.getPassword())) {
            log.warn("Неверный старый пароль при смене пароля для игрока: {}", player.getEmail());
            throw new OldPasswordMismatchException();
        }
        if (passwordEncoder.matches(request.getNewPassword(), player.getPassword())) {
            log.warn("Новый пароль совпадает со старым для игрока: {}", player.getEmail());
            throw new SamePasswordException();
        }

        player.setPassword(passwordEncoder.encode(request.getNewPassword()));
        playerService.save(player);
        log.info("Пароль изменён для игрока: {}", player.getEmail());
    }
}
