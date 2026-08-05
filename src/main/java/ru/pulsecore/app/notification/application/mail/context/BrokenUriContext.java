package ru.pulsecore.app.notification.application.mail.context;

public record BrokenUriContext(
        String to,
        String brokenUri
) implements MailContext{
}
