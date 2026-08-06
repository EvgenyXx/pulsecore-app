package ru.pulsecore.app.player.infrastructure.persistence.entity;

public enum ReportStatus {
    PENDING,    // Ждёт отправки
    SENT,       // Отправлено
    CANCELLED   // Отменено пользователем
}