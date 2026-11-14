package ru.pulsecore.app.admin.api.dto;

import ru.pulsecore.app.player.domain.enums.City;

public record UpdatePlayerRequest(
        String name,
        String email,
        String primaryLeague,
        Boolean pushEnabled,
        Boolean notificationsEnabled,
        String selectedHalls,
        String liveSelectedHalls,
        City city
) {}