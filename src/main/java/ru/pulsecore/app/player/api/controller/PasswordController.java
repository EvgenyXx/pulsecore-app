package ru.pulsecore.app.player.api.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.player.infrastructure.config.SecurityUser;
import ru.pulsecore.app.player.api.AuthApi;
import ru.pulsecore.app.player.api.dto.request.ForgotPasswordRequest;
import ru.pulsecore.app.player.api.dto.request.ResetPasswordRequest;
import ru.pulsecore.app.player.api.dto.request.VerifyPasswordRequest;
import ru.pulsecore.app.shared.dto.MessageResponse;
import ru.pulsecore.app.player.application.player.PlayerProfileService;
import ru.pulsecore.app.player.application.auth.PlayerPasswordResetService;

import java.util.UUID;

@RestController
@RequestMapping(AuthApi.BASE_PATH)
@RequiredArgsConstructor
public class PasswordController {

    private static final String RESET_SESSION_KEY = "reset";

    private final PlayerPasswordResetService passwordResetService;
    private final PlayerProfileService playerProfileService;

    @PostMapping(AuthApi.FORGOT_PASSWORD)
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                                          HttpSession session) {
        var pending = passwordResetService.initiate(request.getEmail());
        session.setAttribute(RESET_SESSION_KEY, pending);
        session.setMaxInactiveInterval(600);
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }

    @PostMapping(AuthApi.RESET_PASSWORD)
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                         HttpSession session) {
        var pending = (PlayerPasswordResetService.Pending) session.getAttribute(RESET_SESSION_KEY);
        if (pending == null) return ResponseEntity.status(400).body(new MessageResponse(AuthApi.CODE_EXPIRED));
        passwordResetService.complete(pending.email(), request.getCode(), pending.code(), request.getPassword());
        session.removeAttribute(RESET_SESSION_KEY);
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }

    @PostMapping(AuthApi.VERIFY_PASSWORD)
    public ResponseEntity<MessageResponse> verifyPassword(@Valid @RequestBody VerifyPasswordRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser user)) {
            return ResponseEntity.status(401).build();
        }
        playerProfileService.verifyPassword(UUID.fromString(user.getPlayerId()), request.getPassword());
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }
}