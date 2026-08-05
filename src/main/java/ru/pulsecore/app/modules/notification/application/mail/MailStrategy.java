package ru.pulsecore.app.modules.notification.application.mail;

import ru.pulsecore.app.modules.notification.application.mail.context.MailContext;

public interface MailStrategy {
    String getType();
    void send(MailContext context);
}