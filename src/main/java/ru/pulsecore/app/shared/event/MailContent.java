package ru.pulsecore.app.shared.event;

import ru.pulsecore.app.notification.application.mail.MailType;
import ru.pulsecore.app.notification.application.mail.context.MailContext;

public record MailContent(
        MailType emailType,
        MailContext context
) {}