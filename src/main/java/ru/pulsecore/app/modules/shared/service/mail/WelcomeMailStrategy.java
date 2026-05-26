package ru.pulsecore.app.modules.shared.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.service.strategy.MailProperties;
import ru.pulsecore.app.modules.player.service.strategy.MailStrategy;
import ru.pulsecore.app.modules.player.service.strategy.MailTypes;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class WelcomeMailStrategy implements MailStrategy {

    private final MailProperties mailProperties;
    private final ResourceLoader resourceLoader;

    @Override
    public String getType() {
        return MailTypes.WELCOME;
    }

    @Override
    public SimpleMailMessage createMessage(String to, Object... args) {
        String name = (String) args[0];

        String firstName = name.contains(" ")
                ? name.substring(name.lastIndexOf(" ") + 1)
                : name;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(to);
        message.setSubject("🏓 " + firstName + ", добро пожаловать в PulseCore!");
        message.setText(getTemplate().replace("{name}", firstName));


        return message;
    }

    private String getTemplate() {
        try {
            return resourceLoader.getResource("classpath:mail/welcome.txt")
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Добро пожаловать в PulseCore!";
        }
    }
}