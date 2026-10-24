package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailType;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;

import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.VerificationContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class VerificationMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public MailType type() {
        return MailType.VERIFICATION;
    }

    @Override
    public void send(MailContext ctx) {
        VerificationContext c = (VerificationContext) ctx;
        String text = templates.format(MailTemplate.VERIFICATION, c.code());
        mailSender.send(MailFormat.TEXT, c.to(),
                "PulseCore — Код подтверждения", text, null, null);
    }
}