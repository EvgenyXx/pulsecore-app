package ru.pulsecore.app.modules.notification_modules.application.mail.context;

public record BroadcastContext(
        String to,
        String text
) implements MailContext {


}