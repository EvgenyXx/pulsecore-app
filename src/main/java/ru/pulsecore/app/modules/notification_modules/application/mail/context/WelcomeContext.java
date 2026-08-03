package ru.pulsecore.app.modules.notification_modules.application.mail.context;

public record WelcomeContext(
        String to,
        String firstName
) implements MailContext {


}