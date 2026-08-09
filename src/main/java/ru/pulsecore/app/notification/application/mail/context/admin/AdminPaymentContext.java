package ru.pulsecore.app.notification.application.mail.context.admin;

import ru.pulsecore.app.notification.application.mail.context.MailContext;

public record AdminPaymentContext(
        String playerName,
        int months,
        String amount,
        String currency
) implements MailContext {


}