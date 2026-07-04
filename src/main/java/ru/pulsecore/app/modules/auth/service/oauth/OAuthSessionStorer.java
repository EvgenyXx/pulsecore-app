package ru.pulsecore.app.modules.auth.service.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class OAuthSessionStorer {

    public void store(HttpServletRequest request, String provider, OAuthDataExtractor.OAuthUserData data) {
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
}