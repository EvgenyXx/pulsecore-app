package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final ChatService chatService;
    private final ChatMentionService chatMentionService;

    public List<ChatMessageDto> getMessages(Long lineupId, Long after) {
        if (after != null) {
            return chatService.getMessagesAfter(lineupId, after);
        }
        return chatService.getMessages(lineupId);
    }

    public ChatMessageDto sendMessage(Long lineupId, ChatMessageDto msg) {
        return chatService.sendMessage(lineupId, msg);
    }

    public List<Map<String, String>> searchPlayers(String q) {
        return chatMentionService.searchPlayers(q);
    }
}