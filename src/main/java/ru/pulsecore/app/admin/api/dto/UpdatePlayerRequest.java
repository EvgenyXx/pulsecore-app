package ru.pulsecore.app.admin.api.dto;

public record UpdatePlayerRequest(
        String name,
        String email,
        String primaryLeague,
        Boolean pushEnabled,
        Boolean notificationsEnabled,
        String selectedHalls,
        String liveSelectedHalls
) {}