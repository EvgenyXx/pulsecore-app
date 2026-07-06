package ru.pulsecore.app.modules.shared.service.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.shared.properties.RememberMeProperties;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RememberMeService {

    private final RememberMeProperties rememberMeProperties;
    private final CookieFactory cookieFactory;

    public void set(HttpServletResponse response, String playerId) {
        cookieFactory.setRememberMe(response, playerId);
    }

    public Optional<String> getPlayerId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();
        return Arrays.stream(cookies)
                .filter(c -> rememberMeProperties.getCookieName().equals(c.getName()))
                .map(Cookie::getValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
    }

    public void refresh(HttpServletResponse response, String playerId) {
        set(response, playerId);
    }

    public void clear(HttpServletResponse response) {
        cookieFactory.clearRememberMe(response);
    }

    public void clearSessionCookie(HttpServletResponse response) {
        cookieFactory.clearSessionCookie(response);
    }
}