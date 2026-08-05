package ru.pulsecore.app.modules.player.application.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ru.pulsecore.app.modules.player.entity.Player;
import ru.pulsecore.app.modules.shared.dto.MailNotificationEvent;
import ru.pulsecore.app.modules.notification.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification.application.mail.context.VerificationContext;
import ru.pulsecore.app.modules.shared.dto.PlayerCreatedEvent;


@Component
@RequiredArgsConstructor
public class RegistrationMailPublisher {

    private static final int RECENT_DAYS = 30;

    private final ApplicationEventPublisher eventPublisher;


    public void sendVerificationCode(String email, String code) {
        eventPublisher.publishEvent(
                new MailNotificationEvent(
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