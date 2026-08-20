package ru.pulsecore.app.notification.application.mail.sender;

import lombok.RequiredArgsConstructor;



import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TextMailSender implements MailSendStrategy {

    private final JavaMailSender mailSender;


    @Override
    public void send(String from,String to, String subject, String text, String fileName, byte[] attachment) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}