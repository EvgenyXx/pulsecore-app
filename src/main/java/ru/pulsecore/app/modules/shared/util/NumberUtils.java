
package ru.pulsecore.app.modules.shared.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtils {

    public static Integer extractInt(String value) {
        if (value == null) return null;
        try {
            return Integer.parseInt(value.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return null;
        }
    }
}