package ru.pulsecore.app.player.application.auth;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.BadCredentialsException;
import ru.pulsecore.app.player.application.role.RoleService;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class RegistrationFacade {

    private final RegistrationValidator validator;
    private final VerificationCodeGenerator codeGenerator;
    private final PlayerFactory playerFactory;
    private final PostRegistrationService postRegistration;
    private final RoleService roleService;
    private final RegistrationMailPublisher mailPublisher;

    public record Pending(String name, String email, String password, String code) implements Serializable {}

    public Pending initiate(String name, String email, String rawPassword) {
        validator.validate(email, name);
        String code = codeGenerator.generate();
        mailPublisher.sendVerificationCode(email, code);
        return new Pending(name, email, rawPassword, code);
    }

    @Transactional
    public Player complete(Pending pending, String code,String ip, String userAgent) {
        if (!pending.code().equals(code)) throw new BadCredentialsException();

        var defaultRole = roleService.findRoleUser();
        Player player = playerFactory.create(pending.name(), pending.email(), pending.password(), defaultRole);
        postRegistration.execute(player);
        mailPublisher.playerCreated(player,ip,userAgent);
        return player;
    }
}