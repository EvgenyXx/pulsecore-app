package ru.pulsecore.app.modules.shared.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.exception.BadResetCodeException;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.shared.service.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.shared.service.mail.MailTypes;

import java.io.Serializable;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class PlayerPasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailStrategyRegistry mailStrategyRegistry;

    public record Pending(String email, String code) implements Serializable {}

    public Pending initiate(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        String code = String.format("%06d", RANDOM.nextInt(999999));
        mailStrategyRegistry.send(MailTypes.PASSWORD_RESET, normalizedEmail, code);
        return new Pending(normalizedEmail, code);
    }

    @Transactional
    public void complete(String email, String code, String expectedCode, String newPassword) {
        if (!expectedCode.equals(code)) {
            throw new BadResetCodeException();
        }
        Player player = playerRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(BadResetCodeException::new);
        player.setPassword(passwordEncoder.encode(newPassword));
        playerRepository.save(player);
    }
}