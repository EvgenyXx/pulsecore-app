// PasswordResetMailStrategy.java
package ru.pulsecore.app.modules.notification_modules.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailStrategy;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTemplateService;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification_modules.application.mail.UniversalMailSender;

import ru.pulsecore.app.modules.notification_modules.application.mail.context.MailContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.context.PasswordResetContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailFormat;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class PasswordResetMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.PASSWORD_RESET;
    }

    @Override
    public void send(MailContext ctx) {
        PasswordResetContext c = (PasswordResetContext) ctx;
        String text = templates.format(MailTemplate.PASSWORD_RESET, c.code());
        mailSender.send(MailFormat.TEXT, c.to(), "PulseCore — Сброс пароля", text, null, null);
    }
}