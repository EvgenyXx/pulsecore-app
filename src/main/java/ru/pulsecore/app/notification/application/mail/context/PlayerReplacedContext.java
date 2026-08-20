package ru.pulsecore.app.notification.application.mail.context;

public record PlayerReplacedContext(
        String to,
        String firstName,
        String tournamentTitle,
        String date,
        String time,
        String hall,
        String league
) implements MailContext {}