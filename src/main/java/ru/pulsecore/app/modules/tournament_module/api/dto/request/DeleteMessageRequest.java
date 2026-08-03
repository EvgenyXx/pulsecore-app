package ru.pulsecore.app.modules.tournament_module.api.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class DeleteMessageRequest {
    private Long messageId;
    private UUID playerId;
}