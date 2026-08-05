package ru.pulsecore.app.player.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException;
import ru.pulsecore.app.player.infrastructure.exception.OAuthOnlyLoginException;
import ru.pulsecore.app.player.infrastructure.exception.PlayerNameAlreadyExistsException;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;

@Component
@RequiredArgsConstructor
public class RegistrationValidator {

    private final PlayerRepository playerRepository;

    public void validate(String email, String name) {
        String normalizedEmail = email.toLowerCase().trim();
        String normalizedName = name.toLowerCase().trim();

        playerRepository.findByEmail(normalizedEmail).ifPresent(player -> {
            if (player.getPassword() == null || player.getPassword().isBlank()) {
                throw new OAuthOnlyLoginException(player.getOauthProvider());
            }
            throw new EmailAlreadyExistsException();
        });

        if (playerRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new PlayerNameAlreadyExistsException();
        }
    }
}