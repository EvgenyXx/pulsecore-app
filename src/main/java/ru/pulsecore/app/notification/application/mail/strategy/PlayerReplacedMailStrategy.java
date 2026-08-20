package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.PlayerReplacedContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class PlayerReplacedMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.PLAYER_REPLACED;
    }

    @Override
    public void send(MailContext ctx) {
        PlayerReplacedContext c = (PlayerReplacedContext) ctx;
        String text = templates.format(
                MailTemplate.PLAYER_REPLACED,
                c.firstName(),
                c.tournamentTitle(),
                c.date(),
                c.time(),
                c.hall(),
                c.league()
        );
        mailSender.send(
                MailFormat.TEXT,
                c.to(),
                "📋 Изменение в составе турнира",
                text,
                null,
                null);
    }
}