package ru.pulsecore.app.shared.exception.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DatabaseConstraint {
    PLAYER_NAME("uk_player_name_normalized"),
    PLAYER_EMAIL("players_email_key"),
    PLAYER_NAME_LEGACY("players_name_key"),
    OAUTH("unique_oauth");

    private final String constraintName;

    public static DatabaseConstraint fromName(String name) {
        if (name == null) return null;
        return Arrays.stream(values())
                .filter(c -> c.constraintName.equals(name))
                .findFirst()
                .orElse(null);
    }
}