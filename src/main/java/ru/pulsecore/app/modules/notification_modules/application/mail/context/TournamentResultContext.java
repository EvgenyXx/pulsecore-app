package ru.pulsecore.app.modules.notification_modules.application.mail.context;

public record TournamentResultContext(
        String to,
        String result
) implements MailContext {


}