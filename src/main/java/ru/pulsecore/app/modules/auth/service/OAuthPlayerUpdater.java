package ru.pulsecore.app.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import ru.pulsecore.app.modules.auth.service.oauth.OAuthDataExtractor;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor//todo удалить
public class OAuthPlayerUpdater {

    private final PlayerRepository playerRepository;

    public Player update(Player player, String provider, OAuthDataExtractor.OAuthUserData data) {
        player.setOauthProvider(provider);
        player.setOauthId(data.oauthId());
        player.setPhone(data.phone());
        player.setAvatarUrl(data.avatar());
        player.setGender(data.gender());
        if (data.birthday() != null) {
            try {
                player.setBirthday(LocalDate.parse(data.birthday()));
            } catch (Exception ignored) {}
        }
        return playerRepository.save(player);
    }
}