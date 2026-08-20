package ru.pulsecore.app.player.application.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import java.util.Optional;


/**
 * Поиск игрока для OAuth авторизации.
 * Ищет по связке провайдер+OAuth ID, затем по email.
 */
@Component
@RequiredArgsConstructor
public class OAuthPlayerFinder {

    private final PlayerSearchService playerSearchService;

    public Optional<Player> find(String provider, String oauthId, String email) {
        Optional<Player> byOAuth = playerSearchService.findByOauthProviderAndOauthId(provider, oauthId);
        if (byOAuth.isPresent()) return byOAuth;
        if (email != null) {
            return playerSearchService.findByEmail(email.toLowerCase().trim());
        }
        return Optional.empty();
    }
}