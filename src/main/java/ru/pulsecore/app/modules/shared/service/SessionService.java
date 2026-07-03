package ru.pulsecore.app.modules.shared.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final CookieFactory cookieFactory;

    public void invalidateCurrentSession() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpSession session = attrs.getRequest().getSession(false);
        if (session != null) session.invalidate();

        SecurityContextHolder.clearContext();

        HttpServletResponse response = attrs.getResponse();
        if (response != null) {
            cookieFactory.clearSessionCookie(response);
        }
    }
}