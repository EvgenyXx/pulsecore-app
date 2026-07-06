package ru.pulsecore.app.modules.player.domain;

public enum ReportStatus {
    PENDING,    // Ждёт отправки
    SENT,       // Отправлено
    CANCELLED   // Отменено пользователем
}