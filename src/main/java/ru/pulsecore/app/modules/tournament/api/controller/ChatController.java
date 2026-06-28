// ChatController.java — удалить getOnline
package ru.pulsecore.app.modules.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.tournament.api.ChatApi;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;
import ru.pulsecore.app.modules.tournament.service.ChatFacade;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ChatApi.BASE_PATH)
@RequiredArgsConstructor
public class ChatController {

    private final ChatFacade chatFacade;

    @GetMapping(ChatApi.LINEUP_ID)
    public List<ChatMessageDto> getMessages(@PathVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId,
                                            @RequestParam(value = "after", required = false) Long after) {
        return chatFacade.getMessages(lineupId, after);
    }

    @PostMapping(ChatApi.LINEUP_ID)
    public ChatMessageDto send(@PathVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId, @RequestBody ChatMessageDto msg) {
        return chatFacade.sendMessage(lineupId, msg);
    }

    @GetMapping(ChatApi.PLAYERS_SEARCH)
    public List<Map<String, String>> searchPlayers(@RequestParam String q) {
        return chatFacade.searchPlayers(q);
    }
}