package ru.pulsecore.app.shared.exception.factory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.pulsecore.app.shared.exception.BaseException;
import ru.pulsecore.app.shared.exception.dto.ErrorResponse;

import java.time.LocalDateTime;

public final class ErrorResponseFactory {

    private ErrorResponseFactory() {}

    public static ResponseEntity<ErrorResponse> build(BaseException e) {
        return ResponseEntity.status(e.getStatus()).body(ErrorResponse.builder()
                .status(e.getStatus().value())
                .error(e.getStatus().getReasonPhrase())
                .message(e.getMessage())
                .type(e.getType())
                .timestamp(LocalDateTime.now())
                .build());
    }

    public static ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build());
    }
}