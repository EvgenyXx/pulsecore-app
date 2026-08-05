package ru.pulsecore.app.player.application.auth.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class OAuthDataExtractor {

    public OAuthUserData extract(String provider, OAuth2User user) {
        if ("yandex".equals(provider)) {
            return extractYandex(user);
        }
        if ("vk".equals(provider)) {
            return extractVk(user);
        }
        return null;
    }

    private OAuthUserData extractYandex(OAuth2User user) {
        return new OAuthUserData(
                Objects.toString(user.getAttribute("id"), ""),
                user.getAttribute("default_email"),
                extractYandexPhone(user),
                extractYandexAvatar(user),
                user.getAttribute("birthday"),
                extractYandexGender(user)
        );
    }

    private String extractYandexPhone(OAuth2User user) {
        Object phoneObj = user.getAttribute("default_phone");
        if (phoneObj instanceof Map<?, ?> m) {
            return (String) m.get("number");
        }
        return null;
    }

    private String extractYandexAvatar(OAuth2User user) {
        String avatarId = user.getAttribute("default_avatar_id");
        if (avatarId != null && !avatarId.isEmpty()) {
            return "https://avatars.yandex.net/get-yapic/" + avatarId + "/islands-200";
        }
        return null;
    }

    private String extractYandexGender(OAuth2User user) {
        String sex = user.getAttribute("sex");
        if ("male".equals(sex)) return "М";
        if ("female".equals(sex)) return "Ж";
        return null;
    }

    private OAuthUserData extractVk(OAuth2User user) {
        return new OAuthUserData(
                Objects.toString(user.getAttribute("user_id"), ""),
                user.getAttribute("email"),
                user.getAttribute("phone"),
                user.getAttribute("avatar"),
                user.getAttribute("birthday"),
                extractVkGender(user.getAttribute("sex"))
        );
    }

    private String extractVkGender(Object sexObj) {
        if (sexObj == null) return null;
        int sex = sexObj instanceof Integer i ? i : Integer.parseInt(sexObj.toString());
        if (sex == 2) return "М";
        if (sex == 1) return "Ж";
        return null;
    }

    public record OAuthUserData(String oauthId, String email, String phone,
                                String avatar, String birthday, String gender) {}
}