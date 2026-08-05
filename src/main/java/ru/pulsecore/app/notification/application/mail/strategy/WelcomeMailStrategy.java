// WelcomeMailStrategy.java
package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;

import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.WelcomeContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class WelcomeMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.WELCOME;
    }

    @Override
    public void send(MailContext ctx) {
        WelcomeContext c = (WelcomeContext) ctx;
        String text = templates.format(MailTemplate.WELCOME, c.firstName());
        mailSender.send(
                MailFormat.TEXT,
                c.to(),
                "🏓 " + c.firstName() + ", добро пожаловать в PulseCore!",
                text,
                null,
                null);
    }
}