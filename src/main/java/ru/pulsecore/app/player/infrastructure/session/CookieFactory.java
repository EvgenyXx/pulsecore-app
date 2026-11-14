package ru.pulsecore.app.player.infrastructure.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.infrastructure.properties.RememberMeProperties;
import ru.pulsecore.app.player.infrastructure.properties.SessionProperties;

@Component
@RequiredArgsConstructor
public class CookieFactory {

    private final SessionProperties session;
    private final RememberMeProperties rememberMe;

    public void setRememberMe(HttpServletResponse response, String value) {
        addCookie(response, rememberMe.cookieName(), value, rememberMe.maxAgeSeconds(), rememberMe.sameSite());
    }

    public void clearRememberMe(HttpServletResponse response) {
        addCookie(response, rememberMe.cookieName(), null, 0, null);
    }

    public void clearSessionCookie(HttpServletResponse response) {
        addCookie(response, session.name(), null, 0, null);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge, String sameSite) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(session.path());
        cookie.setHttpOnly(session.httpOnly());
        cookie.setSecure(session.secure());
        cookie.setMaxAge(maxAge);
        if (sameSite != null) {
            cookie.setAttribute("SameSite", sameSite);
        }
        response.addCookie(cookie);
    }
}