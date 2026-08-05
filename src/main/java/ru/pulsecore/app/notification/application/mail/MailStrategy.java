package ru.pulsecore.app.notification.application.mail;

import ru.pulsecore.app.notification.application.mail.context.MailContext;

public interface MailStrategy {
    String getType();
    void send(MailContext context);
}