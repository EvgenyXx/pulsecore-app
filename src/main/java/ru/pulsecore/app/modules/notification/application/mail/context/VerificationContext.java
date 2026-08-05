package ru.pulsecore.app.modules.notification.application.mail.context;

public record VerificationContext(
        String to,
        String code
) implements MailContext {


}