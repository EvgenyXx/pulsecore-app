package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final ChatService chatService;
    private final ChatMentionService chatMentionService;
    private final ChatWebSocketService chatWebSocketService;

    public List<ChatMessageDto> getMessages(Long lineupId, Long after) {
        if (after != null) {
            return chatService.getMessagesAfter(lineupId, after);
        }
        return chatService.getMessages(lineupId);
    }

    public ChatMessageDto sendMessage(Long lineupId, ChatMessageDto msg) {
        ChatMessageDto saved = chatService.sendMessage(lineupId, msg);
        chatWebSocketService.sendNewMessage(lineupId, saved);
        updateOnline(lineupId);
        return saved;
    }

    public List<Map<String, String>> searchPlayers(String q) {
        return chatMentionService.searchPlayers(q);
    }

    public void deleteMessage(Long messageId, UUID playerId) {
        Long lineupId = chatService.deleteMessage(messageId, playerId);
        chatWebSocketService.sendDelete(lineupId, messageId);
    }

    public void updateMessage(Long messageId, UUID playerId, String newText) {
        Long lineupId = chatService.updateMessage(messageId, playerId, newText);
        chatWebSocketService.sendEdit(lineupId, messageId, newText);
    }

    private void updateOnline(Long lineupId) {
        long count = chatWebSocketService.getOnlineCount(lineupId);
        chatWebSocketService.sendOnlineCount(lineupId, count);
    }
}