package ru.pulsecore.app.modules.notification_modules.application.mail.context;

public record BrokenUriContext(
        String to,
        String brokenUri
) implements MailContext{
}
