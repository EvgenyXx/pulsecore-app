package ru.pulsecore.app.modules.notification.application.mail.context;

public record NewTournamentContext(
        String to,
        String firstName,
        String date,
        String time,
        String hall,
        String league,
        String players,
        String link
) implements MailContext {}