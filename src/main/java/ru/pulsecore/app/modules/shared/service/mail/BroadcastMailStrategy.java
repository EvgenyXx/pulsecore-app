// modules/shared/service/mail/BroadcastMailStrategy.java
package ru.pulsecore.app.modules.shared.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BroadcastMailStrategy implements MailStrategy {

    private final MailProperties mailProperties;

    @Override
    public String getType() {
        return MailTypes.BROADCAST;
    }

    @Override
    public SimpleMailMessage createMessage(String to, Object... args) {
        String text = (String) args[0];

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(to);
        message.setSubject("PulseCore — Уведомление");
        message.setText(text);

        return message;
    }
}