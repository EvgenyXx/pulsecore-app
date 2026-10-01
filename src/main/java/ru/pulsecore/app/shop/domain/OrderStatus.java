package ru.pulsecore.app.shop.domain;

public enum OrderStatus {
    CONFIRMED,    // Собирается (после оплаты)
    ASSEMBLED,    // Собран
    SHIPPED,      // Отправлен
    DONE,         // Получен
    CANCELLED     // Отменён
}