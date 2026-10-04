package ru.pulsecore.app.shop.domain;

import java.util.Set;

public enum OrderStatus {
    CONFIRMED,    // Собирается (после оплаты)
    ASSEMBLED,    // Собран
    SHIPPED,      // Отправлен
    DONE,         // Получен
    CANCELLED;     // Отменён

    public static final Set<OrderStatus> ACTIVE = Set.of(CONFIRMED, ASSEMBLED);

}