package ru.pulsecore.app.shared.event;

import ru.pulsecore.app.notification.application.mail.context.MailContext;

public record MailContent(
        String emailType,
        MailContext context   // ← базовый интерфейс
) {}