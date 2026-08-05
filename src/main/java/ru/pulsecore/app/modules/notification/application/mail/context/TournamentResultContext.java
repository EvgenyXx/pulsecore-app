package ru.pulsecore.app.modules.notification.application.mail.context;

public record TournamentResultContext(
        String to,
        String result
) implements MailContext {


}