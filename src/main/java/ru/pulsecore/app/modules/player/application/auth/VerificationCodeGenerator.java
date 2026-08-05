package ru.pulsecore.app.modules.player.application.auth;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class VerificationCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate() {
        return String.format("%06d", RANDOM.nextInt(999999));
    }
}