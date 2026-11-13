package ru.pulsecore.app.tournament.application.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.exception.ForbiddenException;
import ru.pulsecore.app.tournament.infrastructure.exception.MessageNotFoundException;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.tournament.application.mapping.ChatMessageMapper;
import ru.pulsecore.app.tournament.domain.entity.ChatMessage;
import ru.pulsecore.app.tournament.infrastructure.repository.ChatMessageRepository;
import java.util.List;
import java.util.UUID;

@Slf4j
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

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getMessagesAfter(Long lineupId, Long afterId) {
        return chatMessageRepository.findByIdAfterAndLineupIdOrderByCreatedAtAsc(afterId, lineupId)
                .stream()
                .map(chatMessageMapper::toDto)
                .toList();
    }

    @Transactional
    public Long deleteMessage(Long messageId, UUID playerId) {
        ChatMessage msg = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        if (!msg.getPlayerId().equals(playerId)) {
            throw new ForbiddenException();
        }

        Long lineupId = msg.getLineupId();
        chatMessageRepository.delete(msg);
        return lineupId;
    }

    @Transactional
    public Long updateMessage(Long messageId, UUID playerId, String newText) {
        ChatMessage msg = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        if (!msg.getPlayerId().equals(playerId)) {
            throw new ForbiddenException();
        }

        msg.setMessage(newText);
        msg.setEdited(true);
        chatMessageRepository.save(msg);
        return msg.getLineupId();
    }
}