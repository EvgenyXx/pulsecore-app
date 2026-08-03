// AdminPaymentMailStrategy.java
package ru.pulsecore.app.modules.notification_modules.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailStrategy;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTemplateService;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification_modules.application.mail.UniversalMailSender;

import ru.pulsecore.app.modules.notification_modules.application.mail.context.AdminPaymentContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.context.MailContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailFormat;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class AdminPaymentMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.ADMIN_PAYMENT_RECEIVED;
    }

    @Override
    public void send(MailContext ctx) {
        AdminPaymentContext c = (AdminPaymentContext) ctx;
        String text = templates.format(MailTemplate.ADMIN_PAYMENT,
                c.playerName(), c.months(), c.amount(), c.currency());
        mailSender.send(MailFormat.TEXT, c.to(), "PulseCore — Новая оплата", text, null, null);
    }
}