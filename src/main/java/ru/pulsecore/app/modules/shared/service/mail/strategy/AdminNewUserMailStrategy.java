package ru.pulsecore.app.modules.shared.service.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.service.mail.*;
import ru.pulsecore.app.modules.shared.service.mail.context.AdminNewUserContext;
import ru.pulsecore.app.modules.shared.service.mail.context.MailContext;
import ru.pulsecore.app.modules.shared.service.mail.template.MailFormat;
import ru.pulsecore.app.modules.shared.service.mail.template.MailTemplate;

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