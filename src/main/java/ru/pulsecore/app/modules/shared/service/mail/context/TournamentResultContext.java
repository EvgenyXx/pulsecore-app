package ru.pulsecore.app.modules.shared.service.mail.context;

public record TournamentResultContext(
        String to,
        String result
) implements MailContext {


}