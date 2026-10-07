package ru.pulsecore.app.player.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.VerificationContext;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.shared.dispetcher.MailDispatcher;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.shared.event.PlayerCreatedEvent;

@Component
@RequiredArgsConstructor
public class RegistrationMailPublisher {

    private static final int RECENT_DAYS = 30;

    private final MailDispatcher mailDispatcher;
    private final ApplicationEventPublisher eventPublisher;

    public void sendVerificationCode(String email, String code) {
        mailDispatcher.send(
                email,
                new MailContent(
                        MailTypes.VERIFICATION,
                        new VerificationContext(email, code)
                )
        );
    }


    public void playerCreated(Player player, String ip, String userAgent) {
        eventPublisher.publishEvent(
                new PlayerCreatedEvent(
                        player.getId(),
                        player.getName(),
                        player.getEmail(),
                        RECENT_DAYS,
                        ip,
                        userAgent
                )
        );
    }
}