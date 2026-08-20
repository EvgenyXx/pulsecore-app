package ru.pulsecore.app.shared.security;

import java.util.UUID;

/**
 * Доменный объект текущего пользователя.
 * Не зависит от Spring Security.
 */
public record PlayerPrincipal(
        UUID playerId,
        String email,
        String name
) {}