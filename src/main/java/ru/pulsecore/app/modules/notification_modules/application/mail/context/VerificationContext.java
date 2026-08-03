package ru.pulsecore.app.modules.notification_modules.application.mail.context;

public record VerificationContext(
        String to,
        String code
) implements MailContext {


}