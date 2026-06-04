package ru.pulsecore.app.modules.shared.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.pulsecore.app.modules.shared.properties.SessionProperties;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionProperties sessionProperties;

    public void invalidateCurrentSession() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpSession session = attrs.getRequest().getSession(false);
        if (session != null) session.invalidate();

        SecurityContextHolder.clearContext();

        HttpServletResponse response = attrs.getResponse();
        if (response != null) {
            Cookie cookie = new Cookie(sessionProperties.getName(), null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}