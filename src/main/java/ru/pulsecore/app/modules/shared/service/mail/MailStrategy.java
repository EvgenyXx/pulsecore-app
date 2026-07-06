package ru.pulsecore.app.modules.shared.service.mail;

import ru.pulsecore.app.modules.shared.service.mail.context.MailContext;

public interface MailStrategy {
    String getType();
    void send(MailContext context);
}