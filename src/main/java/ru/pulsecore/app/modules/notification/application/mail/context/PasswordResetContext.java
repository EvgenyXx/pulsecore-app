package ru.pulsecore.app.modules.notification.application.mail.context;

public record PasswordResetContext(
        String to,
        String code
) implements MailContext {


}