package ru.pulsecore.app.notification.application.mail.context;

public record BroadcastContext(
        String to,
        String text
) implements MailContext {


}