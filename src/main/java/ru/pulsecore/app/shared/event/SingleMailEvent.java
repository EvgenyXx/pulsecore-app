package ru.pulsecore.app.shared.event;

public record SingleMailEvent(
        String email,
        MailContent content
) {}