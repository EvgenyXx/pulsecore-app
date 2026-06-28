package ru.pulsecore.app.modules.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.pulsecore.app.modules.tournament.api.ChatApi;
import ru.pulsecore.app.modules.tournament.api.ChatSocketApi;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;
import ru.pulsecore.app.modules.tournament.service.ChatFacade;
import ru.pulsecore.app.modules.tournament.service.ChatService;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatFacade chatFacade;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String CHAT_TOPIC_PREFIX = "/topic/chat/";

    @MessageMapping(ChatSocketApi.SEND)
    public void sendMessage(@DestinationVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId, ChatMessageDto msg) {
        ChatMessageDto saved = chatFacade.sendMessage(lineupId, msg);
        messagingTemplate.convertAndSend(CHAT_TOPIC_PREFIX + lineupId, saved);
    }
}