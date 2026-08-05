package ru.pulsecore.app.notification.application.mail.context;

public record TournamentResultContext(
        String to,
        String result
) implements MailContext {


}