package ru.pulsecore.app.modules.notification_modules.application.mail;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MailTemplateService {

    private final ResourceLoader resourceLoader;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String getTemplate(MailTemplate template) {
        return cache.computeIfAbsent(template.getFileName(), this::loadTemplate);
    }

    @SneakyThrows
    private String loadTemplate(String name) {
        return resourceLoader.getResource("classpath:mail/" + name + ".txt")
                .getContentAsString(StandardCharsets.UTF_8);
    }

    /** Подстановка параметров в шаблон */
    public String format(MailTemplate template, Object... args) {
        return String.format(getTemplate(template), args);
    }
}