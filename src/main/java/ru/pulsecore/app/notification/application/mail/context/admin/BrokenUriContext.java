package ru.pulsecore.app.notification.application.mail.context.admin;

import ru.pulsecore.app.notification.application.mail.context.MailContext;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BrokenUriContext(
        String brokenUri,
        LocalDate date,
        String time,
        Long tournamentId,
        Set<UUID> playerIds
) implements MailContext {
}