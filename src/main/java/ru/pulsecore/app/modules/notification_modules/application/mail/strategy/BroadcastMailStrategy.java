// BroadcastMailStrategy.java
package ru.pulsecore.app.modules.notification_modules.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailStrategy;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification_modules.application.mail.UniversalMailSender;

import ru.pulsecore.app.modules.notification_modules.application.mail.context.BroadcastContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.context.MailContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailFormat;

@Component
@RequiredArgsConstructor
public class BroadcastMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;

    @Override
    public String getType() {
        return MailTypes.BROADCAST;
    }

    @Override
    public void send(MailContext ctx) {
        BroadcastContext c = (BroadcastContext) ctx;
        mailSender.send(MailFormat.TEXT, c.to(), "PulseCore — Уведомление", c.text(), null, null);
    }
}