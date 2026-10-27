package ru.pulsecore.app.shared.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class RegexUtils {

    /**
     * Оставляет только буквы (русские и английские).
     * Убирает пробелы, цифры и любые спецсимволы.
     */
    public final Pattern ONLY_LETTERS = Pattern.compile("[^a-zA-Zа-яА-ЯёЁ]");
}