package ru.pulsecore.app.modules.admin_modules.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.modules.admin_modules.api.AdminApi;
import ru.pulsecore.app.modules.admin_modules.api.dto.BroadcastRequest;
import ru.pulsecore.app.modules.admin_modules.application.BroadcastService;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;

@AdminController
@RequiredArgsConstructor
public class BroadcastController {
//todo вынести логику  отправить через событие в увд
    private final BroadcastService broadcastService;

    @PostMapping(AdminApi.BROADCAST)
    public ResponseEntity<MessageResponse> broadcast(@RequestBody BroadcastRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Сообщение не может быть пустым"));
        }

        return ResponseEntity.ok(
                new MessageResponse(broadcastService.broadcast(request.message()).toMessage())
        );
    }
}