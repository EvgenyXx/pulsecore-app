package ru.pulsecore.app.tournament.application.chat;

import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import java.util.UUID;

public interface ChatMessageService {

    /**
     * Просто написать сообщение в чат
     */
    ChatMessageDto sendMessage(Long lineupId, UUID playerId, String playerName, String message);

    /**
     * Ответить на конкретное сообщение
     */
    ChatMessageDto replyToMessage(Long lineupId, UUID playerId, String playerName, String message, Long replyToId);

    /**
     * Упомянуть игрока (@)
     */
    ChatMessageDto mentionPlayer(Long lineupId, UUID playerId, String playerName, String message, UUID mentionedPlayerId);
}