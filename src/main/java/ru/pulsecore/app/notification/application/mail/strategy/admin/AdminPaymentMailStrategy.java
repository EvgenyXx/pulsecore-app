package ru.pulsecore.app.notification.application.mail.strategy.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;

import ru.pulsecore.app.notification.application.mail.context.admin.AdminPaymentContext;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;

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
        mailSender.adminSendEmail(MailFormat.TEXT,  "PulseCore — Новая оплата", text, null, null);
    }
}