package ru.pulsecore.app.player.application.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.OldPasswordMismatchException;
import ru.pulsecore.app.player.infrastructure.exception.SamePasswordException;
import java.util.UUID;


/**
 * Управление паролем игрока.
 * Проверка текущего пароля и смена на новый.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final PlayerSearchService playerSearchService;
    private final PlayerCommandService  playerCommandService;
    private final PasswordEncoder passwordEncoder;

    public void verifyPassword(UUID id, String rawPassword) {
        Player player = playerSearchService.getById(id);
        if (!passwordEncoder.matches(rawPassword, player.getPassword())) {
            log.warn("Неверный пароль для игрока: {}", player.getEmail());
            throw new OldPasswordMismatchException();
        }
    }

    @Transactional
    public void changePassword(UUID playerId, String oldPassword,String newPassword) {
        Player player = playerSearchService.getById(playerId);

        if (!passwordEncoder.matches(oldPassword, player.getPassword())) {
            log.warn("Неверный старый пароль при смене пароля для игрока: {}", player.getEmail());
            throw new OldPasswordMismatchException();
        }
        if (passwordEncoder.matches(newPassword, player.getPassword())) {
            log.warn("Новый пароль совпадает со старым для игрока: {}", player.getEmail());
            throw new SamePasswordException();
        }

        player.setPassword(passwordEncoder.encode(newPassword));
        playerCommandService.save(player);
        log.info("Пароль изменён для игрока: {}", player.getEmail());
    }
}
