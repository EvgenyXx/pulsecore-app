package ru.pulsecore.app.modules.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.tournament.api.ChatApi;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;
import ru.pulsecore.app.modules.tournament.service.ChatMentionService;
import ru.pulsecore.app.modules.tournament.service.ChatService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ChatApi.BASE_PATH)
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatMentionService chatMentionService;//todo вынести в фасад

    @GetMapping(ChatApi.LINEUP_ID)
    public List<ChatMessageDto> getMessages(@PathVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId,
                                            @RequestParam(value = "after", required = false) Long after) {
        if (after != null) {
            return chatService.getMessagesAfter(lineupId, after);
        }
        return chatService.getMessages(lineupId);
    }

    @PostMapping(ChatApi.LINEUP_ID)
    public ChatMessageDto send(@PathVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId, @RequestBody ChatMessageDto msg) {
        return chatService.sendMessage(lineupId, msg);
    }

    @GetMapping(ChatApi.ONLINE)
    public ResponseEntity<Long> getOnline(@PathVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId) {
        return ResponseEntity.ok(chatService.getOnlineCount(lineupId));
    }

    @GetMapping(ChatApi.PLAYERS_SEARCH)
    public List<Map<String, String>> searchPlayers(@RequestParam String q) {
        return chatMentionService.searchPlayers(q);
    }
}