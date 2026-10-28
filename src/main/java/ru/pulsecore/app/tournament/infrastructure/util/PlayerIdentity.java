package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;
import ru.pulsecore.app.shared.util.RegexUtils;

@UtilityClass
public class PlayerIdentity {

    /**
     * Строгое сравнение двух имён игроков.
     * Оба имени очищаются от пробелов, цифр и спецсимволов,
     * приводятся к нижнему регистру и сравниваются целиком.
     * Примеры:
     * isSamePlayer("Иванов Михаил", "иванов михаил") → true
     * isSamePlayer("Иванов Михаил", "Иванов Михаил Юр") → false
     * isSamePlayer("Иванов Михаил", "Иванов Александр") → false
     */
    public boolean isSamePlayer(String name1, String name2) {
        if (name1 == null || name2 == null) return false;

        String clean1 = cleanName(name1);
        String clean2 = cleanName(name2);

        return clean1.equals(clean2);
    }

    /**
     * Приводит имя к виду "ивановмихаил":
     * оставляет только буквы и переводит в нижний регистр.
     */
    public String cleanName(String name) {
        return RegexUtils.ONLY_LETTERS.matcher(name)
                .replaceAll("")
                .toLowerCase();
    }
}