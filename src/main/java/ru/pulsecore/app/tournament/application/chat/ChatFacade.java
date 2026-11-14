package ru.pulsecore.app.tournament.application.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.api.player.PlayerTournamentClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;

import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final ChatService chatService;
    private final ChatWebSocketService chatWebSocketService;
    private final PlayerTournamentClient playerClient;

    public List<ChatMessageDto> getMessages(Long lineupId, Long after) {
        if (after != null) {
            return chatService.getMessagesAfter(lineupId, after);
        }
        return chatService.getMessages(lineupId);
    }

    public List<PlayerData> searchPlayers(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return playerClient.searchByName(query);
    }

    public void deleteMessage(Long messageId, UUID playerId) {
        Long lineupId = chatService.deleteMessage(messageId, playerId);
        chatWebSocketService.sendDelete(lineupId, messageId);
    }

    public void updateMessage(Long messageId, UUID playerId, String newText) {
        Long lineupId = chatService.updateMessage(messageId, playerId, newText);
        chatWebSocketService.sendEdit(lineupId, messageId, newText);
    }


}