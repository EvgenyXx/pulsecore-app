package ru.pulsecore.app.shared.event;

import ru.pulsecore.app.shared.dto.response.PlayerData;



public record SingleMailEvent(
        PlayerData playerData,
        String email,
        MailContent content
) {}