package ru.pulsecore.app.shared.dto;

public record NotificationInfo(
       boolean canSendEmail,
       boolean canSendPush
) {}