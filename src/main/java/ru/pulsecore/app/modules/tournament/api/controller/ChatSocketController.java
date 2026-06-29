package ru.pulsecore.app.modules.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ru.pulsecore.app.modules.tournament.api.ChatApi;
import ru.pulsecore.app.modules.tournament.api.ChatSocketApi;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;
import ru.pulsecore.app.modules.tournament.service.ChatFacade;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatFacade chatFacade;

    @MessageMapping(ChatSocketApi.SEND)
    public void sendMessage(@DestinationVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId, ChatMessageDto msg) {
        chatFacade.sendMessage(lineupId, msg);
    }
}