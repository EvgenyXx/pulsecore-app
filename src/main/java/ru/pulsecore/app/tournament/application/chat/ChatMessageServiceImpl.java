package ru.pulsecore.app.tournament.application.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dispetcher.PushDispatcher;
import ru.pulsecore.app.shared.event.PushContent;
import ru.pulsecore.app.shared.util.PlayerNameUtils;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.tournament.application.mapping.ChatMessageMapper;
import ru.pulsecore.app.tournament.domain.entity.ChatMessage;
import ru.pulsecore.app.tournament.infrastructure.exception.MessageNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.repository.ChatMessageRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageMapper chatMessageMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final PushDispatcher pushDispatcher;

    @Override
    @Transactional
    public ChatMessageDto sendMessage(Long lineupId, UUID playerId, String playerName, String message) {
        ChatMessage entity = chatMessageMapper.toEntity(lineupId, playerId, playerName, message);
        ChatMessage saved = chatMessageRepository.save(entity);
        return chatMessageMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ChatMessageDto replyToMessage(Long lineupId, UUID playerId, String playerName, String message, Long replyToId) {
        ChatMessage parent = chatMessageRepository.findById(replyToId)
                .orElseThrow(() -> new MessageNotFoundException(replyToId));

        ChatMessage entity = chatMessageMapper.toEntity(lineupId, playerId, playerName, message);
        entity.setReplyTo(parent);
        entity.setReplyToContent(parent.getMessage());
        entity.setReplyToSenderName(parent.getPlayerName());

        ChatMessage saved = chatMessageRepository.save(entity);

        if (!parent.getPlayerId().equals(playerId)) {
            String url = "/#/live/" + lineupId;
            pushDispatcher.send(Map.of(
                    parent.getPlayerId(),
                    new PushContent(PlayerNameUtils.formatName(playerName), message, url, true)
            ));
        }

        return chatMessageMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ChatMessageDto mentionPlayer(Long lineupId, UUID playerId, String playerName, String message, UUID mentionedPlayerId) {
        String cleanMessage = cleanMentionText(message);

        ChatMessage entity = chatMessageMapper.toEntity(lineupId, playerId, playerName, cleanMessage);
        ChatMessage saved = chatMessageRepository.save(entity);

        if (mentionedPlayerId != null && !mentionedPlayerId.equals(playerId)) {
            String url = "/#/live/" + lineupId;
            pushDispatcher.send(Map.of(
                    mentionedPlayerId,
                    new PushContent(PlayerNameUtils.formatName(playerName), cleanMessage, url, true)
            ));
        }

        return chatMessageMapper.toDto(saved);
    }

    private String cleanMentionText(String text) {
        if (text == null || text.isBlank()) return "";

        String[] words = text.trim().split("\\s+");

        if (words.length > 2) {
            return String.join(" ", Arrays.copyOfRange(words, 2, words.length));
        }

        return "";
    }
}