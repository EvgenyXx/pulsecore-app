package ru.pulsecore.app.modules.notification.application.mail.context;

public record AdminPaymentContext(
        String to,
        String playerName,
        int months,
        String amount,
        String currency
) implements MailContext {


}