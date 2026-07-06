package ru.pulsecore.app.modules.shared.service.mail.context;

public record VerificationContext(
        String to,
        String code
) implements MailContext {


}