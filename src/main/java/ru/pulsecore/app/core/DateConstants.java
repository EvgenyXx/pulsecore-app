
package ru.pulsecore.app.core;

import java.time.format.DateTimeFormatter;

public final class DateConstants {
    private DateConstants() {}

    public static final DateTimeFormatter TOURNAMENT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}