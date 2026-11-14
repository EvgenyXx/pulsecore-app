package ru.pulsecore.app.tournament.application.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;

@Service
@RequiredArgsConstructor
public class ChatMessageFacade {

    private final ChatMessageService chatMessageService;
    private final ChatWebSocketService chatWebSocketService;

    public ChatMessageDto handleMessage(Long lineupId, ChatMessageDto msg) {
        ChatMessageDto saved = switch (msg.getType()) {
            case REPLY -> handleReply(lineupId, msg);
            case MENTION -> handleMention(lineupId, msg);
            case SIMPLE -> handleSimple(lineupId, msg);
        };

        chatWebSocketService.sendNewMessage(lineupId, saved);
        updateOnline(lineupId);

        return saved;
    }

    private ChatMessageDto handleReply(Long lineupId, ChatMessageDto msg) {
        return chatMessageService.replyToMessage(
                lineupId,
                msg.getPlayerId(),
                msg.getPlayerName(),
                msg.getMessage(),
                msg.getReplyToId()
        );
    }

    private ChatMessageDto handleMention(Long lineupId, ChatMessageDto msg) {
        return chatMessageService.mentionPlayer(
                lineupId,
                msg.getPlayerId(),
                msg.getPlayerName(),
                msg.getMessage(),
                msg.getMentionedPlayerId()
        );
    }

    private ChatMessageDto handleSimple(Long lineupId, ChatMessageDto msg) {
        return chatMessageService.sendMessage(
                lineupId,
                msg.getPlayerId(),
                msg.getPlayerName(),
                msg.getMessage()
        );
    }

    private void updateOnline(Long lineupId) {
        long count = chatWebSocketService.getOnlineCount(lineupId);
        chatWebSocketService.sendOnlineCount(lineupId, count);
    }
}