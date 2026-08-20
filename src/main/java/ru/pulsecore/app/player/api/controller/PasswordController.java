package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.player.api.dto.request.ForgotPasswordRequest;
import ru.pulsecore.app.player.api.dto.request.ResetPasswordRequest;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.player.application.auth.PlayerPasswordResetService;

@Tag(name = "Auth", description = "Восстановление пароля")
@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PasswordController {

    private static final String RESET_SESSION_KEY = "reset";

    private final PlayerPasswordResetService passwordResetService;

    @Operation(summary = "Запросить сброс пароля")
    @PostMapping(PlayerApi.FORGOT_PASSWORD)
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                                          HttpSession session) {
        var pending = passwordResetService.initiate(request.getEmail());
        session.setAttribute(RESET_SESSION_KEY, pending);
        session.setMaxInactiveInterval(600);
        return ResponseEntity.ok(new MessageResponse(PlayerApi.OK));
    }

    @Operation(summary = "Сбросить пароль с кодом")
    @PostMapping(PlayerApi.RESET_PASSWORD)
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                         HttpSession session) {
        var pending = (PlayerPasswordResetService.Pending) session.getAttribute(RESET_SESSION_KEY);
        if (pending == null) return ResponseEntity.status(400).body(new MessageResponse(PlayerApi.CODE_EXPIRED));
        passwordResetService.complete(pending.email(), request.getCode(), pending.code(), request.getPassword());
        session.removeAttribute(RESET_SESSION_KEY);
        return ResponseEntity.ok(new MessageResponse(PlayerApi.OK));
    }
}