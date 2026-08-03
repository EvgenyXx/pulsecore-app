package ru.pulsecore.app.modules.notification_modules.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailStrategy;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTemplateService;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification_modules.application.mail.UniversalMailSender;

import ru.pulsecore.app.modules.notification_modules.application.mail.context.AdminNewUserContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.context.MailContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailFormat;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class AdminNewUserMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.ADMIN_NEW_USER;
    }

    @Override
    public void send(MailContext ctx) {
        AdminNewUserContext c = (AdminNewUserContext) ctx;
        String text = templates.format(MailTemplate.ADMIN_NEW_USER,
                c.name(), c.email(), c.ip(), c.device(),
                c.os(), c.browser(), c.userAgent());
        mailSender.send(MailFormat.TEXT, c.to(), "PulseCore — Новый игрок", text, null, null);
    }
}