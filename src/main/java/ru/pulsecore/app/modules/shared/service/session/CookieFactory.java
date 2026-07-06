package ru.pulsecore.app.modules.shared.service.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.properties.RememberMeProperties;
import ru.pulsecore.app.modules.shared.properties.SessionProperties;

@Component
@RequiredArgsConstructor
public class CookieFactory {

    private final SessionProperties session;
    private final RememberMeProperties rememberMe;

    public void setRememberMe(HttpServletResponse response, String value) {
        addCookie(response, rememberMe.getCookieName(), value, rememberMe.getMaxAgeSeconds(), rememberMe.getSameSite());
    }

    public void clearRememberMe(HttpServletResponse response) {
        addCookie(response, rememberMe.getCookieName(), null, 0, null);
    }

    public void clearSessionCookie(HttpServletResponse response) {
        addCookie(response, session.getName(), null, 0, null);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge, String sameSite) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(session.getPath());
        cookie.setHttpOnly(session.isHttpOnly());
        cookie.setSecure(session.isSecure());
        cookie.setMaxAge(maxAge);
        if (sameSite != null) {
            cookie.setAttribute("SameSite", sameSite);
        }
        response.addCookie(cookie);
    }
}