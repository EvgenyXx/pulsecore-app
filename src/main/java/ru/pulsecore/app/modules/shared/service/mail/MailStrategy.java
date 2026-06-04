package ru.pulsecore.app.modules.shared.service.mail;

import org.springframework.mail.SimpleMailMessage;

public interface MailStrategy {
    String getType();
    SimpleMailMessage createMessage(String to, Object... args);
}