package ru.pulsecore.app.modules.player.api.dto.response;

import java.time.LocalDateTime;

public record MeResponse(String id, String name, String email, LocalDateTime createdAt, boolean admin, String theme) {}