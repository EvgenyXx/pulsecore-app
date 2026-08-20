package ru.pulsecore.app.notification.application.mail.context;

import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;

public record TournamentScheduleChangedContext(
        String to,
        String firstName,
        TransferInfo transferInfo
) implements MailContext {}