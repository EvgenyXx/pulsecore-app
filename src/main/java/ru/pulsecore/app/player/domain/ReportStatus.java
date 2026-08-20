package ru.pulsecore.app.player.domain;

public enum ReportStatus {
    PENDING,    // Ждёт отправки
    SENT,       // Отправлено
    CANCELLED   // Отменено пользователем
}