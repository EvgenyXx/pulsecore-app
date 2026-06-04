package ru.pulsecore.app.modules.shared.service.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class AdminNewUserMailStrategy implements MailStrategy {

    private final MailProperties mailProperties;

    public AdminNewUserMailStrategy(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    @Override
    public String getType() {
        return MailTypes.ADMIN_NEW_USER;
    }

    @Override
    public SimpleMailMessage createMessage(String to, Object... args) {
        String name = (String) args[0];
        String email = (String) args[1];
        String ip = (String) args[2];
        String userAgent = (String) args[3];
        String device = (String) args[4];
        String os = (String) args[5];
        String browser = (String) args[6];

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailProperties.getFrom());
        msg.setTo(to);
        msg.setSubject("PulseCore — Новый игрок");
        msg.setText(String.format("""
                📥 Новая регистрация
                
                👤 Имя: %s
                📧 Email: %s
                🌐 IP: %s
                📱 Устройство: %s
                💻 ОС: %s
                🖥 Браузер: %s
                🔍 User-Agent: %s
                
                ⏰ Время: %s
                """,
                name, email, ip,
                device, os, browser, userAgent,
                java.time.LocalDateTime.now()));
        return msg;
    }
}