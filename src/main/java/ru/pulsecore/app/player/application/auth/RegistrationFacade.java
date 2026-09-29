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
import ru.pulsecore.app.player.infrastructure.exception.BadCredentialsException;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class RegistrationFacade {

    public static final String PENDING_SESSION_KEY = "pending";

    private final RegistrationValidator validator;
    private final VerificationCodeGenerator codeGenerator;
    private final PlayerCommandService playerCommandService;
    private final TrialSubscriptionActivator postRegistration;
    private final RoleService roleService;
    private final RegistrationMailPublisher mailPublisher;
    private final PlayerMapper playerMapper;

    public record Pending(String name, String email, String password, String code) implements Serializable {
    }

    public Pending initiate(String name, String email, String rawPassword) {
        validator.validate(email, name);
        String code = codeGenerator.generate();
        mailPublisher.sendVerificationCode(email, code);
        return new Pending(name, email, rawPassword, code);
    }


    @Transactional
    public AuthResponse completeRegistration(HttpSession session,
                                             String code,
                                             String ip,
                                             String userAgent) {
        Pending pending = (Pending) session.getAttribute(PENDING_SESSION_KEY);
        if (pending == null) {
            throw new BadCredentialsException();
        }

        Player player = complete(pending, code, ip, userAgent);
        session.removeAttribute(PENDING_SESSION_KEY);
        return playerMapper.toAuthResponse(player);
    }

    @Transactional
    public Player complete(Pending pending, String code, String ip, String userAgent) {
        if (!pending.code().equals(code)) throw new BadCredentialsException();

        var defaultRole = roleService.findRoleUser();
        Player player = playerCommandService.create(
                pending.name(),
                pending.email(),
                pending.password(),
                defaultRole);
        postRegistration.execute(player);
        mailPublisher.playerCreated(player, ip, userAgent);
        return player;
    }
}