package ru.pulsecore.app.modules.shared.exception;

import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException e) {
        // Клиент оборвал соединение — не логируем
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource(NoResourceFoundException e) {
        // Игнорируем тихо — сканеры и боты не засирают логи
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(e.getStatus().value())
                .error(e.getStatus().getReasonPhrase())
                .message(e.getMessage())
                .type(e.getType())  // ← добавь эту строку
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        log.error("Внутренняя ошибка", e);  // ← добавь эту строку
        ErrorResponse response = ErrorResponse.builder()

                .status(500)
                .error("Internal Server Error")
                .message("Внутренняя ошибка сервера")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(500).body(response);
    }


}