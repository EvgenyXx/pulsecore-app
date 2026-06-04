package ru.pulsecore.app.modules.shared.service.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class AdminPaymentMailStrategy implements MailStrategy {

    private final MailProperties mailProperties;

    public AdminPaymentMailStrategy(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    @Override
    public String getType() {
        return MailTypes.ADMIN_PAYMENT_RECEIVED;
    }

    @Override
    public SimpleMailMessage createMessage(String to, Object... args) {
        String playerName = (String) args[0];
        int months = (int) args[1];
        String amount = (String) args[2];
        String currency = (String) args[3];

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailProperties.getFrom());
        msg.setTo(to);
        msg.setSubject("PulseCore — Новая оплата");
        msg.setText(String.format("""
                💰 Новая оплата подписки

                👤 Игрок: %s
                📅 Срок: %d мес.
                💵 Сумма: %s %s

                ⏰ Время: %s
                """,
                playerName, months, amount, currency,
                java.time.LocalDateTime.now()));
        return msg;
    }
}