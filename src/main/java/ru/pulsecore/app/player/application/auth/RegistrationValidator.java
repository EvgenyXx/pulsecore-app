package ru.pulsecore.app.player.application.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException;
import ru.pulsecore.app.player.infrastructure.exception.OAuthOnlyLoginException;
import ru.pulsecore.app.player.infrastructure.exception.PlayerNameAlreadyExistsException;
import ru.pulsecore.app.shared.util.PlayerNameUtils;


/**
 * Валидация пользовател при регистрации
 * проверка, что пользователь зарегистрирован не
 * через яндекс
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationValidator {

    private final PlayerSearchService  playerSearchService;

    public void validate(String email, String name) {
        String normalizedEmail = email.toLowerCase().trim();
        String normalizedName = PlayerNameUtils.normalizeName(name);

        playerSearchService.findByEmail(normalizedEmail).ifPresent(player -> {
            if (player.getPassword() == null || player.getPassword().isBlank()) {
                throw new OAuthOnlyLoginException(player.getOauthProvider());
            }
            throw new EmailAlreadyExistsException();
        });

        if (playerSearchService.existsByNameNormalized(normalizedName)) {
            log.warn("Имя уже занято:{}",normalizedName);
            throw new PlayerNameAlreadyExistsException();
        }
    }
}