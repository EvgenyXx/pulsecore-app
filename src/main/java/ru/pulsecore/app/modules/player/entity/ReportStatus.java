package ru.pulsecore.app.modules.player.entity;

public enum ReportStatus {
    PENDING,    // Ждёт отправки
    SENT,       // Отправлено
    CANCELLED   // Отменено пользователем
}