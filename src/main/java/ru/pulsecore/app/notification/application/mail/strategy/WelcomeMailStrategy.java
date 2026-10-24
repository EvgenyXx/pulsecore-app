package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailType;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;

import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.WelcomeContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;
import ru.pulsecore.app.shared.util.PlayerNameUtils;

@Component
@RequiredArgsConstructor
public class WelcomeMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public MailType type() {
        return MailType.WELCOME;
    }

    @Override
    public void send(MailContext ctx) {
        WelcomeContext c = (WelcomeContext) ctx;
        String text = templates.format(MailTemplate.WELCOME, PlayerNameUtils.extractFirstName(c.firstName()));
        mailSender.send(
                MailFormat.TEXT,
                c.to(),
                "Добро пожаловать в PulseCore!",
                text,
                null,
                null);
    }
}