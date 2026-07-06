package ru.pulsecore.app.modules.shared.service.mail.sender;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PdfMailSender implements MailSendStrategy {

    private final JavaMailSender mailSender;


    @Override
    @SneakyThrows
    public void send(String from,String to, String subject, String text, String fileName, byte[] attachment) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        if (fileName != null && attachment != null) {
            helper.addAttachment(fileName, new ByteArrayResource(attachment));
        }
        mailSender.send(message);
    }
}