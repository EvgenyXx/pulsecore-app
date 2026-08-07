package ru.pulsecore.app.player.application.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuthPlayerFinder {

    private final PlayerRepository playerRepository;

    public Optional<Player> find(String provider, String oauthId, String email) {
        Optional<Player> byOAuth = playerRepository.findByOauthProviderAndOauthId(provider, oauthId);
        if (byOAuth.isPresent()) return byOAuth;
        if (email != null) {
            return playerRepository.findByEmail(email.toLowerCase().trim());
        }
        return Optional.empty();
    }
}