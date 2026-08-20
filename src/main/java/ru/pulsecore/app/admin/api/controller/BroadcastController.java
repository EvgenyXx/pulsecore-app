package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.api.dto.request.BroadcastRequest;
import ru.pulsecore.app.admin.application.BroadcastService;
import ru.pulsecore.app.shared.dto.response.MessageResponse;

@Tag(name = "Admin", description = "Массовые рассылки")
@AdminController
@RequiredArgsConstructor
public class BroadcastController {

    private final BroadcastService broadcastService;

    @Operation(summary = "Отправить сообщение всем игрокам")
    @PostMapping(AdminApi.BROADCAST)
    public ResponseEntity<MessageResponse> broadcast(@RequestBody BroadcastRequest request) {
        return ResponseEntity.ok(
                new MessageResponse(broadcastService.broadcast(request.message()).toMessage())
        );
    }
}