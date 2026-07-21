package ru.pulsecore.app.modules.shared.service.mail.context;

public record BrokenUriContext(
        String to,
        String brokenUri
) implements MailContext{
}
