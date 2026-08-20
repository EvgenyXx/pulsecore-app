package ru.pulsecore.app.notification.application.mail.context.admin;

import ru.pulsecore.app.notification.application.mail.context.MailContext;

public record MastersApiUnavailableContext(
        String text
) implements MailContext {
}
