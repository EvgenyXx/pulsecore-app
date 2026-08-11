package ru.pulsecore.app.notification.client;


import java.util.UUID;

/**
 * Клиент модуля игроков
 */
public interface PlayerClient {
    boolean togglePushEnabled(UUID playerId);
    boolean isPushEnabled(UUID playerId);
}