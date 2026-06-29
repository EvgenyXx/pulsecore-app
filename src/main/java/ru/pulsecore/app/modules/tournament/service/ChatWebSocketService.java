package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.tournament.domain.ChatEventType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.pulsecore.app.modules.tournament.api.ChatSocketApi.*;

@Service
@RequiredArgsConstructor
public class ChatWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

    public void sendNewMessage(Long lineupId, Object message) {
        messagingTemplate.convertAndSend(TOPIC_CHAT + lineupId, message);
    }

    public void sendDelete(Long lineupId, Long messageId) {
        messagingTemplate.convertAndSend(TOPIC_CHAT + lineupId,
                Map.of(KEY_TYPE, ChatEventType.DELETE.asType(), KEY_MESSAGE_ID, messageId));
    }

    public void sendEdit(Long lineupId, Long messageId, String newText) {
        messagingTemplate.convertAndSend(TOPIC_CHAT + lineupId,
                Map.of(KEY_TYPE, ChatEventType.EDIT.asType(), KEY_MESSAGE_ID, messageId, KEY_MESSAGE, newText));
    }

    public void sendOnlineCount(Long lineupId, long count) {
        messagingTemplate.convertAndSend(TOPIC_CHAT + lineupId + TOPIC_ONLINE, count);
    }

    public long getOnlineCount(Long lineupId) {
        String topic = TOPIC_CHAT + lineupId;
        return userRegistry.getUsers().stream()
                .filter(user -> user.getSessions().stream()
                        .anyMatch(s -> s.getSubscriptions().stream()
                                .anyMatch(sub -> sub.getDestination().equals(topic))))
                .count();
    }

    public Set<Long> getActiveLineupIds() {
        return userRegistry.getUsers().stream()
                .flatMap(user -> user.getSessions().stream())
                .flatMap(session -> session.getSubscriptions().stream())
                .map(SimpSubscription::getDestination)
                .filter(dest -> dest.startsWith(TOPIC_CHAT) && !dest.contains(TOPIC_ONLINE))
                .map(dest -> dest.replace(TOPIC_CHAT, ""))
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    public Map<Long, Long> getAllOnlineCounts() {
        return getActiveLineupIds().stream()
                .collect(Collectors.toMap(id -> id, this::getOnlineCount));
    }
}