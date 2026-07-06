package ru.pulsecore.app.modules.shared.service.mail.context;

public record BroadcastContext(
        String to,
        String text
) implements MailContext {


}