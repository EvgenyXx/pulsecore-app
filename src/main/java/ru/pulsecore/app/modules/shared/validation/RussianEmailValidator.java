package ru.pulsecore.app.modules.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class RussianEmailValidator implements ConstraintValidator<RussianEmail, String> {

    // Разрешённые российские домены
    private static final Set<String> ALLOWED_DOMAINS = Set.of(
            "mail.ru", "list.ru", "bk.ru", "inbox.ru",
            "yandex.ru", "yandex.com", "ya.ru",
            "rambler.ru", "lenta.ru", "autorambler.ru", "ro.ru", "rumbler.ru", "internet.ru"
    );

    // Запрещённые иностранные домены (для понятного сообщения об ошибке)
    private static final Set<String> FORBIDDEN_DOMAINS = Set.of(
            "gmail.com", "googlemail.com",
            "outlook.com", "hotmail.com", "live.com", "msn.com",
            "yahoo.com", "yahoo.fr", "yahoo.co.uk",
            "aol.com",
            "protonmail.com", "proton.me",
            "icloud.com", "me.com", "mac.com",
            "zoho.com", "fastmail.com", "gmx.com", "gmx.de",
            "tutanota.com", "tutanota.de"
    );

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String domain = extractDomain(email);
        if (domain == null) {
            return false;
        }

        // Разрешённые российские домены
        if (ALLOWED_DOMAINS.contains(domain)) {
            return true;
        }

        // Запрещённые иностранные домены — выдаём понятную ошибку
        if (FORBIDDEN_DOMAINS.contains(domain)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Регистрация с почтовым доменом @" + domain + " запрещена. Используйте российскую почту (mail.ru, yandex.ru, rambler.ru и др.)"
            ).addConstraintViolation();
            return false;
        }

        // Неизвестный домен — тоже запрещаем
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "Почтовый домен @" + domain + " не поддерживается. Используйте российскую почту (mail.ru, yandex.ru, rambler.ru и др.)"
        ).addConstraintViolation();
        return false;
    }

    private String extractDomain(String email) {
        int atIndex = email.lastIndexOf('@');
        if (atIndex == -1 || atIndex == email.length() - 1) {
            return null;
        }
        return email.substring(atIndex + 1).toLowerCase();
    }
}