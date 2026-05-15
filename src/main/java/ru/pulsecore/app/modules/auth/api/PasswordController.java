package ru.pulsecore.app.modules.auth.api;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.config.SecurityUser;
import ru.pulsecore.app.modules.auth.api.dto.*;
import ru.pulsecore.app.modules.player.api.dto.MessageResponse;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.player.service.auth.PlayerPasswordResetService;

import java.util.UUID;

@RestController
@RequestMapping(AuthApi.BASE_PATH)
@RequiredArgsConstructor
public class PasswordController {

    private final PlayerPasswordResetService passwordResetService;
    private final PlayerService playerService;

    @PostMapping(AuthApi.FORGOT_PASSWORD)
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                                          HttpSession session) {
        var pending = passwordResetService.initiate(request.getEmail());
        session.setAttribute("reset", pending);
        session.setMaxInactiveInterval(600);
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }

    @PostMapping(AuthApi.RESET_PASSWORD)
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                         HttpSession session) {
        var pending = (PlayerPasswordResetService.Pending) session.getAttribute("reset");
        if (pending == null) return ResponseEntity.status(400).body(new MessageResponse(AuthApi.CODE_EXPIRED));
        passwordResetService.complete(pending.email(), request.getCode(), pending.code(), request.getPassword());
        session.removeAttribute("reset");
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }

    @PostMapping(AuthApi.VERIFY_PASSWORD)
    public ResponseEntity<MessageResponse> verifyPassword(@Valid @RequestBody VerifyPasswordRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser user)) {
            return ResponseEntity.status(401).build();
        }
        playerService.verifyPassword(UUID.fromString(user.getPlayerId()), request.getPassword());
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }
}