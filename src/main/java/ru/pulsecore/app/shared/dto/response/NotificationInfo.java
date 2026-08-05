package ru.pulsecore.app.shared.dto.response;

public record NotificationInfo(
       boolean canSendEmail,
       boolean canSendPush
) {}