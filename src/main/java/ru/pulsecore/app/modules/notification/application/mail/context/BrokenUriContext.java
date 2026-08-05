package ru.pulsecore.app.modules.notification.application.mail.context;

public record BrokenUriContext(
        String to,
        String brokenUri
) implements MailContext{
}
