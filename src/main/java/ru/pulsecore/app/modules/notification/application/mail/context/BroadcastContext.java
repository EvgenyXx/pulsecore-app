package ru.pulsecore.app.modules.notification.application.mail.context;

public record BroadcastContext(
        String to,
        String text
) implements MailContext {


}