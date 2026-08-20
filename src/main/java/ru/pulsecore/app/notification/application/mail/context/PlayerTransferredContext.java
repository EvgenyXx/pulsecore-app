package ru.pulsecore.app.notification.application.mail.context;

import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;

public record PlayerTransferredContext(
        String to,
        String firstName,
        TransferInfo transferInfo
) implements MailContext {}