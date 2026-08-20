package ru.pulsecore.app.tournament.application.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.application.WebPushService;
import ru.pulsecore.app.shared.dto.response.PlayerData;
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
    private final PlayerClient playerClient;
    private final WebPushService webPushService;

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
            if (player != null && !player.playerId().equals(msg.getPlayerId())) {
                mentionedIds.add(player.playerId());
            }


        }

        for (UUID playerId : mentionedIds) {
            try {
                webPushService.sendToPlayer(
                        playerId,
                        "💬 " + msg.getPlayerName(),
                        msg.getMessage(),
                        "/live/" + lineupId
                );
                log.info("Push-уведомление отправлено игроку {} за упоминание в чате {}", playerId, lineupId);
            } catch (Exception e) {
                log.warn("Не удалось отправить push за упоминание: {}", e.getMessage());
            }
        }
    }
}