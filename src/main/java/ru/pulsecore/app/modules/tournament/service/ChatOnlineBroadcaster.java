package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class ChatOnlineBroadcaster {

    private final ChatWebSocketService chatWebSocketService;

    @Scheduled(fixedRate = 5000)
    public void broadcastOnlineCounts() {
        Set<Long> activeLineupIds = chatWebSocketService.getActiveLineupIds();
        for (Long lineupId : activeLineupIds) {
            long count = chatWebSocketService.getOnlineCount(lineupId);
            chatWebSocketService.sendOnlineCount(lineupId, count);
        }
    }
}