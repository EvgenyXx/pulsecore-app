package ru.pulsecore.app.modules.shared.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.exception.BadCredentialsException;
import ru.pulsecore.app.modules.player.service.role.RoleService;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class RegistrationFacade {

    private final RegistrationValidator validator;
    private final VerificationCodeGenerator codeGenerator;
    private final RegistrationMailService mailService;
    private final PlayerFactory playerFactory;
    private final PostRegistrationService postRegistration;
    private final RoleService roleService;

    public record Pending(String name, String email, String password, String code) implements Serializable {}

    public Pending initiate(String name, String email, String rawPassword) {
        validator.validate(email, name);
        String code = codeGenerator.generate();
        mailService.sendVerificationCode(email, code);
        return new Pending(name, email, rawPassword, code);
    }

    @Transactional
    public Player complete(Pending pending, String code, HttpServletRequest request) {
        if (!pending.code().equals(code)) throw new BadCredentialsException();

        var defaultRole = roleService.findRoleUser();
        Player player = playerFactory.create(pending.name(), pending.email(), pending.password(), defaultRole);
        postRegistration.execute(player, request);
        return player;
    }
}