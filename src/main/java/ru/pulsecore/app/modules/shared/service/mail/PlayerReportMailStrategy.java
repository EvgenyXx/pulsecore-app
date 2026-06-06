// src/main/java/ru/pulsecore/app/modules/shared/service/mail/PlayerReportMailStrategy.java
package ru.pulsecore.app.modules.shared.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayerReportMailStrategy implements MailStrategy {

    private final MailProperties mailProperties;

    @Override
    public String getType() {
        return MailTypes.PLAYER_REPORT;
    }

    @Override
    public SimpleMailMessage createMessage(String to, Object... args) {
        String report = (String) args[0];

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(to);
        message.setSubject("📊 PulseCore — Еженедельный отчёт");
        message.setText(report);
        return message;
    }
}