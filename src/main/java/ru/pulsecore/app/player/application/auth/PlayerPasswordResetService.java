package ru.pulsecore.app.player.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.BadResetCodeException;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.PasswordResetContext;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import java.io.Serializable;
import java.security.SecureRandom;

/**
 * Сброс пароля игрока.
 * Инициирует сброс (отправка кода на email), завершает сброс (установка нового пароля).
 */
@Service
@RequiredArgsConstructor
public class PlayerPasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final PlayerCommandService  commandService;
    private final PlayerSearchService  searchService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher  eventPublisher;

    public record Pending(String email, String code) implements Serializable {
    }

    public Pending initiate(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        String code = String.format("%06d", RANDOM.nextInt(999999));
        eventPublisher.publishEvent(
                new MailNotificationEvent(
                        MailTypes.PASSWORD_RESET,
                        new PasswordResetContext(normalizedEmail, code)
                )
        );

        return new Pending(normalizedEmail, code);
    }

    @Transactional
    public void complete(String email, String code, String expectedCode, String newPassword) {
        if (!expectedCode.equals(code)) {
            throw new BadResetCodeException();
        }
        Player player = searchService.findByEmail(email.toLowerCase().trim())
                .orElseThrow(BadResetCodeException::new);
        player.setPassword(passwordEncoder.encode(newPassword));
        commandService.save(player);
    }
}