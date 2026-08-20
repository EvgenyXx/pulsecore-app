package ru.pulsecore.app.notification.application.mail.context;



public record CanceledTournamentContext (
        String to,
        String time,
        String data,
        String link
)implements MailContext{
}
