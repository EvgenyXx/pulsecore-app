package ru.pulsecore.app.player.application.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.api.dto.response.AuthResponse;
import ru.pulsecore.app.player.application.mapper.PlayerMapper;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.role.RoleService;
import ru.pulsecore.app.player.application.subscription.TrialSubscriptionActivator;
import ru.pulsecore.app.player.domain.Player;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class RegistrationFacade {

    private static final int CODE_TTL_SECONDS = 600;

    private final RegistrationSessionExtractor sessionExtractor;
    private final RegistrationValidator validator;
    private final VerificationCodeGenerator codeGenerator;
    private final PlayerCommandService playerCommandService;
    private final TrialSubscriptionActivator postRegistration;
    private final RoleService roleService;
    private final RegistrationMailPublisher mailPublisher;
    private final PlayerMapper playerMapper;

    public record Pending(String name, String email, String password, String code) implements Serializable {
    }

    public void initiate(String name, String email, String rawPassword, HttpSession session) {
        validator.validate(email, name);
        String code = codeGenerator.generate();
        mailPublisher.sendVerificationCode(email, code);

        var pending = new Pending(name, email, rawPassword, code);
        session.setAttribute(RegistrationSessionExtractor.PENDING_SESSION_KEY, pending);
        session.setMaxInactiveInterval(CODE_TTL_SECONDS);
    }

    @Transactional
    public AuthResponse completeRegistration(HttpSession session, String code, String ip, String userAgent) {
        Pending pending = sessionExtractor.extract(session, code);
        var defaultRole = roleService.findRoleUser();
        Player player = playerCommandService.create(
                pending.name(),
                pending.email(),
                pending.password(),
                defaultRole);
        postRegistration.execute(player);
        mailPublisher.playerCreated(player, ip, userAgent);


        session.removeAttribute(RegistrationSessionExtractor.PENDING_SESSION_KEY);

        return playerMapper.toAuthResponse(player);
    }

}