// BroadcastMailStrategy.java
package ru.pulsecore.app.modules.shared.service.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.service.mail.*;
import ru.pulsecore.app.modules.shared.service.mail.context.BroadcastContext;
import ru.pulsecore.app.modules.shared.service.mail.context.MailContext;
import ru.pulsecore.app.modules.shared.service.mail.template.MailFormat;

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