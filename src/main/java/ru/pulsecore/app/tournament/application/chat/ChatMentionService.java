package ru.pulsecore.app.tournament.application.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMentionService {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([\\p{L}]+)\\s+([\\p{L}]+)");
    private static final String PUSH_TITLE = "Вас упомянули в чате";


    private final PlayerClient playerClient;
    private final ApplicationEventPublisher eventPublisher;

    public List<PlayerData> searchPlayers(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return playerClient.searchByName(query);
    }

    public void processMentions(Long lineupId, ChatMessageDto msg) {
        if (msg.getMessage() == null) return;
        Set<UUID> mentionedIds = new HashSet<>();
        Matcher matcher = MENTION_PATTERN.matcher(msg.getMessage());

        while (matcher.find()) {
            String fullName = matcher.group(1) + " " + matcher.group(2);
            PlayerData player = playerClient.findByName(fullName);
            if (player != null && !player.id().equals(msg.getPlayerId())) {
                mentionedIds.add(player.id());
            }


        }

        sendPushForMentions(mentionedIds,msg,lineupId);
    }

    private void sendPushForMentions(Set<UUID> mentionedIds, ChatMessageDto msg, Long lineupId) {
        String body = msg.getPlayerName() + ": " + msg.getMessage();
        String url = "/dashboard#/live/" + lineupId;

        mentionedIds.forEach(playerId ->
                eventPublisher.publishEvent(
                        new PushNotificationEvent(
                                playerId,
                                PUSH_TITLE,
                                body,
                                url
                        )
                )
        );
        log.debug("Опубликовано push-событий за упоминание: count={}, lineup={}",
                mentionedIds.size(), lineupId);
    }
}