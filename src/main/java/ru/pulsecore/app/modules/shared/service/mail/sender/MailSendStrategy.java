package ru.pulsecore.app.modules.shared.service.mail.sender;

public interface MailSendStrategy {
    void send(String from ,String to, String subject, String text, String fileName, byte[] attachment);
}