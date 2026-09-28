package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.player.api.dto.response.AuthResponse;
import ru.pulsecore.app.player.api.dto.request.RegisterRequest;
import ru.pulsecore.app.player.api.dto.request.VerifyEmailRequest;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.player.application.auth.RegistrationFacade;

@Tag(name = "Auth", description = "Регистрация и подтверждение email")
@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class RegistrationController {

    private static final String PENDING_SESSION_KEY = "pending";

    private final RegistrationFacade registrationFacade;


    @Operation(summary = "Зарегистрировать нового пользователя")
    @PostMapping(PlayerApi.REGISTER)
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request,
                                                    HttpSession session) {
        var pending = registrationFacade.initiate(request.getName(), request.getEmail(), request.getPassword());
        session.setAttribute(PENDING_SESSION_KEY, pending);
        session.setMaxInactiveInterval(600);
        return ResponseEntity.ok(new MessageResponse(PlayerApi.OK));
    }

    @Operation(summary = "Подтвердить email кодом")
    @PostMapping(PlayerApi.VERIFY_EMAIL)
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request,
                                                    HttpSession session,
                                                    HttpServletRequest httpRequest) {

        AuthResponse response = registrationFacade.completeRegistration(
            session,
            request.getCode(),
            httpRequest.getRemoteAddr(),
            httpRequest.getHeader("User-Agent")
    );
        return ResponseEntity.ok(response);
    }
}