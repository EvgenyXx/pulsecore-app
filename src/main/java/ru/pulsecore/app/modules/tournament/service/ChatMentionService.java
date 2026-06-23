package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.push.service.WebPushService;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMentionService {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([\\p{L}]+)\\s+([\\p{L}]+)");
    private final PlayerRepository playerRepository;
    private final WebPushService webPushService;

    public List<Map<String, String>> searchPlayers(String query) {
        if (query == null || query.isBlank()) return List.of();
        return playerRepository.searchByName(query).stream()
                .map(p -> Map.of("id", p.getId().toString(), "name", p.getName()))
                .toList();
    }

    public void processMentions(Long lineupId, ChatMessageDto msg) {
        if (msg.getMessage() == null) return;
        Set<UUID> mentionedIds = new HashSet<>();
        Matcher matcher = MENTION_PATTERN.matcher(msg.getMessage());

        while (matcher.find()) {
            String fullName = matcher.group(1) + " " + matcher.group(2);
            playerRepository.findByNameIgnoreCase(fullName).ifPresent(player -> {
                if (!player.getId().equals(msg.getPlayerId())) {
                    mentionedIds.add(player.getId());
                }
            });
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