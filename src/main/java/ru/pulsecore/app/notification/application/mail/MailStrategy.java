package ru.pulsecore.app.notification.application.mail;

import ru.pulsecore.app.notification.application.mail.context.MailContext;

public interface MailStrategy {
    MailType type();
    void send(MailContext context);
}