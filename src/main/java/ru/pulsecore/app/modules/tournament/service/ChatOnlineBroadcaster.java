package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatOnlineBroadcaster {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 30000)
    public void broadcastOnlineCounts() {
        List<Long> activeLineupIds = chatService.getActiveLineupIds();
        for (Long lineupId : activeLineupIds) {
            long count = chatService.getOnlineCount(lineupId);
            messagingTemplate.convertAndSend("/topic/chat/" + lineupId + "/online", count);
        }
    }
}