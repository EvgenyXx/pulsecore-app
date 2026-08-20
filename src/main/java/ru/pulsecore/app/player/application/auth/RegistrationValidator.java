package ru.pulsecore.app.player.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException;
import ru.pulsecore.app.player.infrastructure.exception.OAuthOnlyLoginException;
import ru.pulsecore.app.player.infrastructure.exception.PlayerNameAlreadyExistsException;


/**
 * Валидация пользовател при регистрации
 * проверка, что пользователь зарегистрирован не
 * через яндекс
 */
@Component
@RequiredArgsConstructor
public class RegistrationValidator {

    private final PlayerSearchService  playerSearchService;

    public void validate(String email, String name) {
        String normalizedEmail = email.toLowerCase().trim();
        String normalizedName = name.toLowerCase().trim();

        playerSearchService.findByEmail(normalizedEmail).ifPresent(player -> {
            if (player.getPassword() == null || player.getPassword().isBlank()) {
                throw new OAuthOnlyLoginException(player.getOauthProvider());
            }
            throw new EmailAlreadyExistsException();
        });

        if (playerSearchService.existsByName(normalizedName)) {
            throw new PlayerNameAlreadyExistsException();
        }
    }
}