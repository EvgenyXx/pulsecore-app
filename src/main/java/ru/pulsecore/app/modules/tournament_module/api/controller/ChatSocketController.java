package ru.pulsecore.app.modules.tournament_module.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ru.pulsecore.app.modules.tournament_module.api.ChatApi;
import ru.pulsecore.app.modules.tournament_module.api.ChatSocketApi;
import ru.pulsecore.app.modules.tournament_module.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.modules.tournament_module.application.chat.ChatFacade;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatFacade chatFacade;

    @MessageMapping(ChatSocketApi.SEND)
    public void sendMessage(@DestinationVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId, ChatMessageDto msg) {
        chatFacade.sendMessage(lineupId, msg);
    }
}