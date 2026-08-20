package ru.pulsecore.app.player.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.BadCredentialsException;
import ru.pulsecore.app.player.infrastructure.exception.OAuthOnlyLoginException;


/**
 * Аутентификация игрока по email и паролю.
 * Проверяет что пользователь не OAuth-only и пароль совпадает.
 * Бросает BadCredentialsException при неверных данных, OAuthOnlyLoginException для OAuth-пользователей.
 */
@Service
@RequiredArgsConstructor
public class PlayerAuthenticationService {

    private final PlayerSearchService playerSearchService;
    private final PasswordEncoder passwordEncoder;

    public Player authenticate(String email, String rawPassword) {
        String normalizedEmail = email.toLowerCase().trim();
        Player player = playerSearchService.findByEmail(normalizedEmail)
                .orElseThrow(BadCredentialsException::new);

        checkOAuthOnly(player);
        checkPassword(rawPassword, player);
        return player;
    }

    private void checkOAuthOnly(Player player) {
        if (player.getPassword() == null || player.getPassword().isBlank()) {
            throw new OAuthOnlyLoginException(player.getOauthProvider());
        }
    }

    private void checkPassword(String rawPassword, Player player) {
        if (!passwordEncoder.matches(rawPassword, player.getPassword())) {
            throw new BadCredentialsException();
        }
    }
}