package ru.pulsecore.app.modules.notification_modules.application.mail.context;

public record PasswordResetContext(
        String to,
        String code
) implements MailContext {


}