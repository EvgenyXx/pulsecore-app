// VerificationMailStrategy.java
package ru.pulsecore.app.modules.shared.service.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.service.mail.*;
import ru.pulsecore.app.modules.shared.service.mail.context.MailContext;
import ru.pulsecore.app.modules.shared.service.mail.context.VerificationContext;
import ru.pulsecore.app.modules.shared.service.mail.template.MailFormat;
import ru.pulsecore.app.modules.shared.service.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class VerificationMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.VERIFICATION;
    }

    @Override
    public void send(MailContext ctx) {
        VerificationContext c = (VerificationContext) ctx;
        String text = templates.format(MailTemplate.VERIFICATION, c.code());
        mailSender.send(MailFormat.TEXT, c.to(), "PulseCore — Код подтверждения", text, null, null);
    }
}