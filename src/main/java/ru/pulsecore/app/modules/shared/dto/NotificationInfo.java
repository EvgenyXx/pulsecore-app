package ru.pulsecore.app.modules.shared.dto;

public record NotificationInfo(
       boolean canSendEmail,
       boolean canSendPush
) {}