package ru.pulsecore.app.player.application.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.infrastructure.exception.BadCredentialsException;

@Component
@RequiredArgsConstructor
public class RegistrationSessionExtractor {

    public static final String PENDING_SESSION_KEY = "pending";

    public RegistrationFacade.Pending extract(HttpSession session, String code) {
        RegistrationFacade.Pending pending = (RegistrationFacade.Pending) session.getAttribute(PENDING_SESSION_KEY);

        if (pending == null) {
            throw new BadCredentialsException.SessionExpired();
        }

        if (!pending.code().equals(code)) {
            throw new BadCredentialsException.InvalidCode();
        }

        return pending;
    }
}