package ru.pulsecore.app.modules.shared.service.mail.context;

public record WelcomeContext(
        String to,
        String firstName
) implements MailContext {


}