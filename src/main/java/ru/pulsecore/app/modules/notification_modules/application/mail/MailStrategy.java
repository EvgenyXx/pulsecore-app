package ru.pulsecore.app.modules.notification_modules.application.mail;

import ru.pulsecore.app.modules.notification_modules.application.mail.context.MailContext;

public interface MailStrategy {
    String getType();
    void send(MailContext context);
}