package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;
import ru.pulsecore.app.modules.tournament.mapper.ChatMessageMapper;
import ru.pulsecore.app.modules.tournament.persistence.entity.ChatMessage;
import ru.pulsecore.app.modules.tournament.persistence.repository.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getMessages(Long lineupId) {
        return chatMessageRepository.findByLineupIdOrderByCreatedAtAsc(lineupId)
                .stream()
                .map(chatMessageMapper::toDto)
                .toList();
    }

    @Transactional
    public ChatMessageDto sendMessage(Long lineupId, ChatMessageDto msg) {
        ChatMessage entity = ChatMessage.builder()
                .lineupId(lineupId)
                .playerId(msg.getPlayerId())
                .playerName(msg.getPlayerName())
                .message(msg.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        if (msg.getReplyToId() != null) {
            chatMessageRepository.findById(msg.getReplyToId()).ifPresent(replyTo -> {
                entity.setReplyTo(replyTo);
                entity.setReplyToContent(replyTo.getMessage());
                entity.setReplyToSenderName(replyTo.getPlayerName());
            });
        }

        ChatMessage saved = chatMessageRepository.save(entity);
        return chatMessageMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public long getOnlineCount(Long lineupId) {
        return chatMessageRepository.countDistinctPlayerIdByLineupIdAndCreatedAtAfter(
                lineupId,
                LocalDateTime.now().minusMinutes(2)
        );
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getMessagesAfter(Long lineupId, Long afterId) {
        return chatMessageRepository.findByIdAfterAndLineupIdOrderByCreatedAtAsc(afterId, lineupId)
                .stream()
                .map(chatMessageMapper::toDto)
                .toList();
    }
}