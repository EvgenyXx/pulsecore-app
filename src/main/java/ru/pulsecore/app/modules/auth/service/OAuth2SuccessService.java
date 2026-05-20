// ==================== OAuth2SuccessService.java ====================
package ru.pulsecore.app.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.auth.api.PlayerLoginService;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2SuccessService {

    private final PlayerRepository playerRepository;
    private final PlayerLoginService playerLoginService;

    public void handle(OAuth2AuthenticationToken token,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {

        OAuth2User user = token.getPrincipal();
        String provider = token.getAuthorizedClientRegistrationId();
        OAuthUserData data = extractUserData(provider, user);

        if (data == null) {
            log.warn("Unsupported OAuth provider: {}", provider);
            response.sendRedirect("/");
            return;
        }

        Optional<Player> existingPlayer = findExistingPlayer(provider, data);

        if (existingPlayer.isPresent()) {
            updateAndLogin(existingPlayer.get(), provider, data, request, response);
        } else {
            storeInSession(request, provider, data);
            response.sendRedirect("/oauth-finish.html");
        }
    }

    // ── Извлечение данных ──
    private OAuthUserData extractUserData(String provider, OAuth2User user) {
        if ("yandex".equals(provider)) {
            return extractYandexData(user);
        }
        if ("vk".equals(provider)) {
            return extractVkData(user);
        }
        return null;
    }

    private OAuthUserData extractYandexData(OAuth2User user) {
        log.debug("Yandex attributes: {}", user.getAttributes());

        String phone = null;
        Object phoneObj = user.getAttribute("default_phone");
        if (phoneObj instanceof Map<?, ?> m) {
            phone = (String) m.get("number");
        }

        String avatar = null;
        String avatarId = user.getAttribute("default_avatar_id");
        if (avatarId != null && !avatarId.isEmpty()) {
            avatar = "https://avatars.yandex.net/get-yapic/" + avatarId + "/islands-200";
        }

        String gender = null;
        String sex = user.getAttribute("sex");
        if ("male".equals(sex)) gender = "М";
        else if ("female".equals(sex)) gender = "Ж";

        return new OAuthUserData(
                user.getAttribute("id"),
                user.getAttribute("default_email"),
                phone,
                avatar,
                user.getAttribute("birthday"),
                gender
        );
    }

    private OAuthUserData extractVkData(OAuth2User user) {
        log.debug("VK attributes: {}", user.getAttributes());

        String oauthId = String.valueOf(user.getAttribute("user_id"));
        String email = user.getAttribute("email");
        String phone = user.getAttribute("phone");
        String avatar = user.getAttribute("avatar");
        String birthday = user.getAttribute("birthday");

        String gender = null;
        Object sexObj = user.getAttribute("sex");
        if (sexObj != null) {
            int sex = sexObj instanceof Integer ? (Integer) sexObj : Integer.parseInt(sexObj.toString());
            gender = sex == 2 ? "М" : sex == 1 ? "Ж" : null;
        }

        String firstName = user.getAttribute("first_name");
        String lastName = user.getAttribute("last_name");
        String name = (firstName != null && lastName != null) ? firstName + " " + lastName : null;

        return new OAuthUserData(
                oauthId,
                email,
                phone,
                avatar,
                birthday,
                gender
        );
    }

    // ── Поиск игрока ──
    private Optional<Player> findExistingPlayer(String provider, OAuthUserData data) {
        Optional<Player> player = playerRepository.findByOauthProviderAndOauthId(provider, data.oauthId());
        if (player.isEmpty() && data.email() != null) {
            player = playerRepository.findByEmail(data.email().toLowerCase().trim());
        }
        return player;
    }

    // ── Обновление и вход ──
    private void updateAndLogin(Player player, String provider, OAuthUserData data,
                                HttpServletRequest request, HttpServletResponse response) throws IOException {
        player.setOauthProvider(provider);
        player.setOauthId(data.oauthId());
        player.setPhone(data.phone());
        player.setAvatarUrl(data.avatar());
        player.setGender(data.gender());
        if (data.birthday() != null) {
            try {
                player.setBirthday(LocalDate.parse(data.birthday()));
            } catch (Exception e) {
                log.warn("Failed to parse birthday: {}", data.birthday());
            }
        }
        playerRepository.save(player);
        playerLoginService.login(player, request, response);
    }

    // ── Сохранение в сессию для нового пользователя ──
    private void storeInSession(HttpServletRequest request, String provider, OAuthUserData data) {
        HttpSession session = request.getSession(true);
        session.setAttribute("oauth_email", data.email());
        session.setAttribute("oauth_provider", provider);
        session.setAttribute("oauth_id", data.oauthId());
        session.setAttribute("oauth_phone", data.phone());
        session.setAttribute("oauth_avatar", data.avatar());
        session.setAttribute("oauth_birthday", data.birthday());
        session.setAttribute("oauth_gender", data.gender());
        session.setMaxInactiveInterval(600);
    }

    // ── DTO ──
    private record OAuthUserData(String oauthId, String email, String phone,
                                 String avatar, String birthday, String gender) {}
}