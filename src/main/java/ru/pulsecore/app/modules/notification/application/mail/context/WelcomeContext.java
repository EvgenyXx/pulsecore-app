package ru.pulsecore.app.modules.notification.application.mail.context;

public record WelcomeContext(
        String to,
        String firstName
) implements MailContext {


}