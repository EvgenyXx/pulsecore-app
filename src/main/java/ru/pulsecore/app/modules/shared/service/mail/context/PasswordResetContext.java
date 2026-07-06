package ru.pulsecore.app.modules.shared.service.mail.context;

public record PasswordResetContext(
        String to,
        String code
) implements MailContext {


}