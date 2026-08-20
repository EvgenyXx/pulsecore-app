package ru.pulsecore.app.notification.application.mail.context;

public record PasswordResetContext(
        String to,
        String code
) implements MailContext {


}