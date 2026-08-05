// 1. OAuthSessionExtractor.java
package ru.pulsecore.app.player.application.auth.oauth;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class OAuthSessionExtractor {

    private static final String[] SESSION_KEYS = {
            "oauth_email", "oauth_provider", "oauth_id",
            "oauth_phone", "oauth_avatar", "oauth_birthday", "oauth_gender"
    };

    public OAuthSessionData extract(HttpSession session) {
        return new OAuthSessionData(
                attr(session, "oauth_email"),
                attr(session, "oauth_provider"),
                attr(session, "oauth_id"),
                attr(session, "oauth_phone"),
                attr(session, "oauth_avatar"),
                attr(session, "oauth_birthday"),
                attr(session, "oauth_gender")
        );
    }

    public void clear(HttpSession session) {
        for (String key : SESSION_KEYS) session.removeAttribute(key);
    }

    private String attr(HttpSession session, String key) {
        return (String) session.getAttribute(key);
    }

    public record OAuthSessionData(String email, String provider, String oauthId,
                                   String phone, String avatar, String birthday, String gender) {}
}