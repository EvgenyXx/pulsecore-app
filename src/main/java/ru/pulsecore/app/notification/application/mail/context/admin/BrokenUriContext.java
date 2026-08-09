package ru.pulsecore.app.notification.application.mail.context.admin;

import ru.pulsecore.app.notification.application.mail.context.MailContext;

public record BrokenUriContext(
        String brokenUri
) implements MailContext {
}
