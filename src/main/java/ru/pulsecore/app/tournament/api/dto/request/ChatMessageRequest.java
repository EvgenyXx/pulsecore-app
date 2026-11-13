package ru.pulsecore.app.tournament.api.dto.request;

import ru.pulsecore.app.tournament.domain.enums.ChatMessageType;
import java.util.UUID;

public record ChatMessageRequest(
    String message,
    ChatMessageType type,
    Long replyToId,
    UUID mentionedPlayerId
) {}