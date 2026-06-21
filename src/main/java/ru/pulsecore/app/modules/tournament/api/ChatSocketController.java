package ru.pulsecore.app.modules.tournament.api;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;
import ru.pulsecore.app.modules.tournament.service.ChatService;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping(ChatSocketApi.SEND)
    public void sendMessage(@DestinationVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId, ChatMessageDto msg) {
        ChatMessageDto saved = chatService.sendMessage(lineupId, msg);
        messagingTemplate.convertAndSend("/topic/chat/" + lineupId, saved);
    }
}