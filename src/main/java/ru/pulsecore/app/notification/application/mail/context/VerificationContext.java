package ru.pulsecore.app.notification.application.mail.context;

public record VerificationContext(
        String to,
        String code
) implements MailContext {


}