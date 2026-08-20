package ru.pulsecore.app.notification.application.mail.context;

public record WelcomeContext(
        String to,
        String firstName
) implements MailContext {


}